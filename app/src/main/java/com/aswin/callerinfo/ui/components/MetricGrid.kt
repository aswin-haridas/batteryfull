package com.aswin.callerinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aswin.callerinfo.data.model.DashboardAnalytics
import com.aswin.callerinfo.ui.theme.BorderSubtle
import com.aswin.callerinfo.ui.theme.PastelAliceBlue
import com.aswin.callerinfo.ui.theme.PastelHoneydew
import com.aswin.callerinfo.ui.theme.PastelVanilla
import com.aswin.callerinfo.ui.theme.SurfaceCard
import com.aswin.callerinfo.ui.theme.SurfaceDark
import com.aswin.callerinfo.ui.theme.TextMuted

@Composable
fun MetricGrid(
    analytics: DashboardAnalytics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                bgColor = PastelHoneydew,
                label = "Total Talk Time",
                value = analytics.formattedTotalDuration,
                trend = "Avg ${analytics.formattedAvgDuration}/call",
                borderColor = null
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                bgColor = PastelVanilla,
                label = "Total Calls",
                value = analytics.totalCalls.toString(),
                trend = "${analytics.contactsCount} unique contacts",
                borderColor = null
            )
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                bgColor = PastelAliceBlue,
                label = "Incoming vs Out",
                value = "${analytics.incomingCalls} / ${analytics.outgoingCalls}",
                trend = "${analytics.incomingCalls + analytics.outgoingCalls} connected",
                borderColor = null
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                bgColor = SurfaceCard,
                label = "Missed Calls",
                value = analytics.missedCalls.toString(),
                trend = "${analytics.rejectedCalls} rejected",
                borderColor = BorderSubtle
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    bgColor: Color,
    label: String,
    value: String,
    trend: String,
    borderColor: Color? = null
) {
    val shape = RoundedCornerShape(24.dp)
    val baseModifier = if (borderColor != null) {
        modifier
            .clip(shape)
            .background(bgColor)
            .border(1.dp, borderColor, shape)
    } else {
        modifier
            .clip(shape)
            .background(bgColor)
    }

    Column(
        modifier = baseModifier.padding(18.dp)
    ) {
        // Trailing top arrow
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NorthEast,
                    contentDescription = null,
                    tint = SurfaceDark,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SurfaceDark
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = SurfaceDark.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = trend,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted
        )
    }
}
