package com.aswin.callerinfo.ui.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aswin.callerinfo.data.model.ContactAnalytics
import com.aswin.callerinfo.data.model.DashboardAnalytics
import com.aswin.callerinfo.data.model.TimeFilter
import com.aswin.callerinfo.data.repository.CallLogRepository
import com.aswin.callerinfo.data.repository.MockCallDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val isDemoMode: Boolean = false,
    val selectedFilter: TimeFilter = TimeFilter.THIS_MONTH,
    val analytics: DashboardAnalytics = DashboardAnalytics(),
    val selectedContact: ContactAnalytics? = null
)

class CallAnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CallLogRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        checkPermissionAndLoad()
    }

    fun checkPermissionAndLoad() {
        val hasCallLogPermission = ContextCompat.checkSelfPermission(
            getApplication(),
            android.Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED

        _uiState.update { it.copy(hasPermission = hasCallLogPermission) }

        if (hasCallLogPermission) {
            loadLiveData()
        } else {
            // Default to demo mode so user immediately sees UI
            _uiState.update {
                it.copy(
                    isDemoMode = true,
                    analytics = MockCallDataProvider.getSampleDashboard()
                )
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasPermission = granted) }
        if (granted) {
            _uiState.update { it.copy(isDemoMode = false) }
            loadLiveData()
        }
    }

    fun setFilter(filter: TimeFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        if (_uiState.value.isDemoMode) {
            // Load demo dashboard
            _uiState.update {
                it.copy(analytics = MockCallDataProvider.getSampleDashboard())
            }
        } else {
            loadLiveData()
        }
    }

    fun toggleDemoMode() {
        val nextDemo = !_uiState.value.isDemoMode
        _uiState.update { it.copy(isDemoMode = nextDemo) }
        if (nextDemo) {
            _uiState.update {
                it.copy(analytics = MockCallDataProvider.getSampleDashboard())
            }
        } else {
            if (_uiState.value.hasPermission) {
                loadLiveData()
            }
        }
    }

    fun refresh() {
        if (_uiState.value.isDemoMode) {
            _uiState.update {
                it.copy(analytics = MockCallDataProvider.getSampleDashboard())
            }
        } else {
            loadLiveData()
        }
    }

    fun selectContact(contact: ContactAnalytics?) {
        _uiState.update { it.copy(selectedContact = contact) }
    }

    private fun loadLiveData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.getAnalytics(_uiState.value.selectedFilter)
            // If live data has zero records (e.g. empty emulator or clean phone), fall back to demo mode if empty
            if (result.totalCalls == 0 && !_uiState.value.hasPermission) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isDemoMode = true,
                        analytics = MockCallDataProvider.getSampleDashboard()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        analytics = result
                    )
                }
            }
        }
    }
}
