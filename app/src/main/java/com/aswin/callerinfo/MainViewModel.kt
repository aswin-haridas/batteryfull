package com.aswin.callerinfo

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MonitorState(
    val threshold: Int = 80,
    val folderUri: Uri? = null,
    val isRunning: Boolean = false
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("battery_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        MonitorState(
            threshold = prefs.getInt("threshold", 80),
            folderUri = prefs.getString("folder_uri", null)?.let { Uri.parse(it) }
        )
    )
    val state = _state.asStateFlow()

    fun setThreshold(v: Int) {
        prefs.edit().putInt("threshold", v).apply()
        _state.update { it.copy(threshold = v) }
    }

    fun setFolderUri(uri: Uri) {
        prefs.edit().putString("folder_uri", uri.toString()).apply()
        _state.update { it.copy(folderUri = uri) }
    }

    fun setRunning(running: Boolean) {
        _state.update { it.copy(isRunning = running) }
    }
}
