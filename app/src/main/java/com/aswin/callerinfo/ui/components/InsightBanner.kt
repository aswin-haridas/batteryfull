package com.aswin.callerinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aswin.callerinfo.data.model.DashboardAnalytics
import com.aswin.callerinfo.ui.theme.PastelVanilla
import com.aswin.callerinfo.ui.theme.SurfaceDark

@Composable
fun InsightBanner(
    analytics: DashboardAnalytics,
    onViewTopCaller: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topContact = analytics.topContacts.firstOrNull()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(PastelVanilla)
            .clickable { onViewTopCaller() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Key Insight",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SurfaceDark.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (topContact != null) {
                Text(
                    text = "Most active caller is ${topContact.displayName} with ${topContact.formattedTotalDuration} total talk time.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 21.sp,
                    color = SurfaceDark
                )
            } else {
                Text(
                    text = "No calls found for this period. Tap refresh or toggle demo mode.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = SurfaceDark
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 14.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "View top caller",
                tint = PastelVanilla,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
