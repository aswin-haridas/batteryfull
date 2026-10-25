package com.aswin.callerinfo

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
import androidx.core.app.NotificationCompat

class BatteryMonitorService : Service() {

    companion object {
        const val CHANNEL_ID = "battery_monitor"
        const val NOTIF_ID = 1
        const val EXTRA_THRESHOLD = "threshold"
        const val EXTRA_FOLDER_URI = "folder_uri"
        private const val COOLDOWN_MS = 30 * 60 * 1000L
    }

    private var threshold = 80
    private var folderUri: Uri? = null
    private var lastPlayedAt = 0L
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
                if (now - lastPlayedAt >= COOLDOWN_MS) {
                    playRandom()
                    lastPlayedAt = now
                    updateNotification("Battery at $pct% — alert played")
                } else {
                    updateNotification("Battery at $pct% ≥ $threshold% (cooldown)")
                }
            } else {
                val chargingStr = if (isCharging) "charging" else "not charging"
                updateNotification("Battery $pct% ($chargingStr), threshold $threshold%")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        threshold = intent?.getIntExtra(EXTRA_THRESHOLD, 80) ?: 80
        folderUri = intent?.getStringExtra(EXTRA_FOLDER_URI)?.let { Uri.parse(it) }

        startForeground(NOTIF_ID, buildNotification("Monitoring battery…"))
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(batteryReceiver)
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun playRandom() {
        val uri = folderUri ?: return
        val children = try {
            contentResolver.query(
                androidx.documentfile.provider.DocumentFile.fromTreeUri(this, uri)!!.uri
                    .let { treeToChildrenUri(it) },
                arrayOf(
                    android.provider.DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                    android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE
                ),
                null, null, null
            )
        } catch (_: Exception) { null } ?: return

        val audioUris = mutableListOf<Uri>()
        children.use { c ->
            val idCol = c.getColumnIndexOrThrow(android.provider.DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val mimeCol = c.getColumnIndexOrThrow(android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE)
            while (c.moveToNext()) {
                val mime = c.getString(mimeCol) ?: continue
                if (mime.startsWith("audio/")) {
                    val docId = c.getString(idCol)
                    audioUris += android.provider.DocumentsContract.buildDocumentUriUsingTree(uri, docId)
                }
            }
        }

        val pick = audioUris.randomOrNull() ?: return
        player?.release()
        player = MediaPlayer().apply {
            try {
                setDataSource(this@BatteryMonitorService, pick)
                prepare()
                start()
                setOnCompletionListener { it.release(); player = null }
            } catch (_: Exception) {
                release(); player = null
            }
        }
    }

    private fun treeToChildrenUri(treeUri: Uri): Uri =
        android.provider.DocumentsContract.buildChildDocumentsUriUsingTree(
            treeUri,
            android.provider.DocumentsContract.getTreeDocumentId(treeUri)
        )

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
            .setContentTitle("Battery Full Alert")
            .setContentText(text)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIF_ID, buildNotification(text))
    }
}
