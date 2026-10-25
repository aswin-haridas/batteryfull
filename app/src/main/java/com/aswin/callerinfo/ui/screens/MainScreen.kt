package com.aswin.callerinfo.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aswin.callerinfo.BatteryMonitorService
import com.aswin.callerinfo.MainViewModel
import com.aswin.callerinfo.ui.theme.*

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val ctx = LocalContext.current
    val state by viewModel.state.collectAsState()

    val folderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        ctx.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        viewModel.setFolderUri(uri)
    }

    fun toggleService() {
        val intent = Intent(ctx, BatteryMonitorService::class.java).apply {
            putExtra(BatteryMonitorService.EXTRA_THRESHOLD, state.threshold)
            putExtra(BatteryMonitorService.EXTRA_FOLDER_URI, state.folderUri?.toString())
        }
        if (state.isRunning) {
            ctx.stopService(intent)
        } else {
            ctx.startForegroundService(intent)
        }
        viewModel.setRunning(!state.isRunning)
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    "Battery Full",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceDark
                )
            }
        }
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Status chip
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(if (state.isRunning) PrimaryLime else BorderSubtle)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    if (state.isRunning) "● Monitoring active" else "○ Not running",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurfaceDark
                )
            }

            // Threshold card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = SurfaceDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Alert threshold",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SurfaceDark
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            "${state.threshold}%",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceDark
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Slider(
                        value = state.threshold.toFloat(),
                        onValueChange = { viewModel.setThreshold(it.toInt()) },
                        valueRange = 10f..100f,
                        steps = 89,
                        colors = SliderDefaults.colors(
                            thumbColor = SurfaceDark,
                            activeTrackColor = PrimaryLime,
                            inactiveTrackColor = BorderSubtle
                        ),
                        enabled = !state.isRunning
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("10%", fontSize = 11.sp, color = TextMuted)
                        Text("100%", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            // Folder picker card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = SurfaceDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Sound folder",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SurfaceDark
                        )
                        Text(
                            state.folderUri?.lastPathSegment?.substringAfterLast(':')
                                ?: "No folder selected",
                            fontSize = 12.sp,
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = { folderPicker.launch(null) },
                        enabled = !state.isRunning
                    ) {
                        Text(
                            if (state.folderUri == null) "Choose" else "Change",
                            color = SurfaceDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Text(
                "A random audio file from the selected folder will play when charging and battery ≥ ${state.threshold}%. Re-alerts every 30 minutes if still charging.",
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(Modifier.weight(1f))

            // Start / stop button
            Button(
                onClick = { toggleService() },
                enabled = state.folderUri != null || state.isRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isRunning) MissedRed else SurfaceDark,
                    contentColor = if (state.isRunning) SurfaceCard else PrimaryLime,
                    disabledContainerColor = BorderSubtle,
                    disabledContentColor = TextMuted
                )
            ) {
                Text(
                    if (state.isRunning) "Stop monitoring" else "Start monitoring",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
