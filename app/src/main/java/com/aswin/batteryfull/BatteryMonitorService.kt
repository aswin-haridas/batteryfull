package com.aswin.batteryfull

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.BatteryManager
import android.os.IBinder
import android.os.SystemClock
import android.provider.DocumentsContract
import androidx.core.app.NotificationCompat

class BatteryMonitorService : Service() {

    companion object {
        const val CHANNEL_ID = "battery_monitor"
        const val NOTIF_ID = 1
        const val PREFS = "battery_prefs"
        private const val COOLDOWN_MS = 30 * 60 * 1000L

        @Volatile
        var isRunning = false
            private set
    }

    private var threshold = 80
    private var folderUri: Uri? = null
    private var lastPlayedAt = 0L
    private var lastText: String? = null
    private var player: MediaPlayer? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            val pct = if (scale > 0) (level * 100) / scale else level

            if (isCharging && pct >= threshold) {
                val now = SystemClock.elapsedRealtime()
                if (lastPlayedAt == 0L || now - lastPlayedAt >= COOLDOWN_MS) {
                    playRandom()
                    lastPlayedAt = now
                    updateNotification("Battery at $pct% — alert played")
                } else {
                    updateNotification("Battery at $pct% ≥ $threshold% (cooldown)")
                }
            } else {
                // Unplugged: next plug-in above threshold alerts immediately.
                if (!isCharging) lastPlayedAt = 0L
                val chargingStr = if (isCharging) "charging" else "not charging"
                updateNotification("Battery $pct% ($chargingStr), threshold $threshold%")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIF_ID, buildNotification("Monitoring battery…"))
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Read from prefs so a START_STICKY restart (null intent) keeps the user's settings.
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        threshold = prefs.getInt("threshold", 80)
        folderUri = prefs.getString("folder_uri", null)?.let(Uri::parse)
        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        unregisterReceiver(batteryReceiver)
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun playRandom() {
        val uri = folderUri ?: return
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri, DocumentsContract.getTreeDocumentId(uri)
        )
        val audioUris = try {
            contentResolver.query(
                childrenUri,
                arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_MIME_TYPE),
                null, null, null
            )?.use { c ->
                buildList<Uri> {
                    while (c.moveToNext()) {
                        if (c.getString(1)?.startsWith("audio/") == true) {
                            add(DocumentsContract.buildDocumentUriUsingTree(uri, c.getString(0)))
                        }
                    }
                }
            }
        } catch (_: Exception) { null }

        val pick = audioUris?.randomOrNull() ?: return
        player?.release()
        player = try {
            MediaPlayer().apply {
                setDataSource(this@BatteryMonitorService, pick)
                setOnCompletionListener { it.release(); player = null }
                setOnPreparedListener { it.start() }
                prepareAsync()
            }
        } catch (_: Exception) { null }
    }

    private fun createChannel() {
        val ch = NotificationChannel(CHANNEL_ID, "Battery Monitor", NotificationManager.IMPORTANCE_LOW)
        ch.description = "Runs in background to alert when battery reaches threshold"
        getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
    }

    private fun buildNotification(text: String): Notification {
        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setContentIntent(pi)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    // ACTION_BATTERY_CHANGED fires on every voltage/temp tick; only repost when the text changes.
    private fun updateNotification(text: String) {
        if (text == lastText) return
        lastText = text
        getSystemService(NotificationManager::class.java)
            .notify(NOTIF_ID, buildNotification(text))
    }
}
