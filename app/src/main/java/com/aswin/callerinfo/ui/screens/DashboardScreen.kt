package com.aswin.callerinfo.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aswin.callerinfo.ui.components.CallChartCard
import com.aswin.callerinfo.ui.components.ContactCard
import com.aswin.callerinfo.ui.components.ContactDetailSheet
import com.aswin.callerinfo.ui.components.FilterPills
import com.aswin.callerinfo.ui.components.InsightBanner
import com.aswin.callerinfo.ui.components.MetricGrid
import com.aswin.callerinfo.ui.components.MultiSegmentCard
import com.aswin.callerinfo.ui.components.TopBar
import com.aswin.callerinfo.ui.theme.AppBackground
import com.aswin.callerinfo.ui.theme.PastelVanilla
import com.aswin.callerinfo.ui.theme.PrimaryLime
import com.aswin.callerinfo.ui.theme.SurfaceDark
import com.aswin.callerinfo.ui.theme.TextMuted
import com.aswin.callerinfo.ui.viewmodel.CallAnalyticsViewModel

@Composable
fun DashboardScreen(
    viewModel: CallAnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val callLogGranted = permissions[android.Manifest.permission.READ_CALL_LOG] == true
        viewModel.onPermissionResult(callLogGranted)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBackground,
        topBar = {
            TopBar(
                isDemoMode = uiState.isDemoMode,
                onToggleDemoMode = { viewModel.toggleDemoMode() },
                onRefresh = { viewModel.refresh() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Screen Header & Greeting (Section 4.2)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    Text(
                        text = "Hi Miranda,",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "here's what's happening with your calls.",
                        fontSize = 16.sp,
                        color = TextMuted
                    )
                }
            }

            // Segmented Filter Pills (Section 4.3)
            item {
                FilterPills(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = { viewModel.setFilter(it) },
                    modifier = Modifier.padding(horizontal = 0.dp)
                )
            }

            // Permission Request Banner (if not granted)
            if (!uiState.hasPermission) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceDark)
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = PrimaryLime,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Permission Notice",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryLime
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Viewing live call durations requires Call Log access. Currently previewing sample analytics.",
                                fontSize = 13.sp,
                                color = AppBackground.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.READ_CALL_LOG,
                                            android.Manifest.permission.READ_CONTACTS
                                        )
                                    )
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryLime,
                                    contentColor = SurfaceDark
                                )
                            ) {
                                Text(
                                    text = "Grant Call Log Permission",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SurfaceDark)
                    }
                }
            } else {
                // Multi-Segment Progress Card (Section 4.4)
                item {
                    MultiSegmentCard(analytics = uiState.analytics)
                }

                // Action / Banner Card (Section 4.5)
                item {
                    InsightBanner(
                        analytics = uiState.analytics,
                        onViewTopCaller = {
                            uiState.analytics.topContacts.firstOrNull()?.let {
                                viewModel.selectContact(it)
                            }
                        }
                    )
                }

                // 2x2 Metric Grid (Section 4.6)
                item {
                    MetricGrid(analytics = uiState.analytics)
                }

                // Activity Chart Card (Section 4.7)
                item {
                    CallChartCard(
                        activity = uiState.analytics.monthlyActivity,
                        totalFormatted = uiState.analytics.formattedTotalDuration
                    )
                }

                // Section Header: Top Contacts Leaderboard
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Top Contacts by Talk Time",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceDark
                            )
                            Text(
                                text = "Ranked by call duration",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PastelVanilla)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${uiState.analytics.topContacts.size} contacts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SurfaceDark
                            )
                        }
                    }
                }

                // Top Contacts List
                itemsIndexed(uiState.analytics.topContacts) { index, contact ->
                    ContactCard(
                        contact = contact,
                        rank = index + 1,
                        onClick = { viewModel.selectContact(contact) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Detail Bottom Sheet
    uiState.selectedContact?.let { contact ->
        ContactDetailSheet(
            contact = contact,
            onDismiss = { viewModel.selectContact(null) }
        )
    }
}
