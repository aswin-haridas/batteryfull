package com.aswin.callerinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.aswin.callerinfo.ui.screens.DashboardScreen
import com.aswin.callerinfo.ui.theme.CallerInfoTheme
import com.aswin.callerinfo.ui.viewmodel.CallAnalyticsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CallAnalyticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallerInfoTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
