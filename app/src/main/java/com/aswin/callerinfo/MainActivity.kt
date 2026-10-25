package com.aswin.callerinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.aswin.callerinfo.ui.screens.MainScreen
import com.aswin.callerinfo.ui.theme.CallerInfoTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallerInfoTheme {
                MainScreen(viewModel)
            }
        }
    }
}
