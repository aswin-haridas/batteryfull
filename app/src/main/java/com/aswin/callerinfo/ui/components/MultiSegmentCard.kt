package com.aswin.callerinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun MultiSegmentCard(
    analytics: DashboardAnalytics,
    modifier: Modifier = Modifier
) {
    val total = (analytics.outgoingCalls + analytics.incomingCalls + analytics.missedCalls + analytics.rejectedCalls).coerceAtLeast(1)

    val outWeight = (analytics.outgoingCalls.toFloat() / total).coerceAtLeast(0.02f)
    val inWeight = (analytics.incomingCalls.toFloat() / total).coerceAtLeast(0.02f)
    val missedWeight = (analytics.missedCalls.toFloat() / total).coerceAtLeast(0.02f)
    val rejWeight = (analytics.rejectedCalls.toFloat() / total).coerceAtLeast(0.02f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Calls Breakdown",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurfaceDark
                )
                Text(
                    text = "${analytics.totalCalls} total logged",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PastelHoneydew)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${analytics.contactsCount} contacts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurfaceDark
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Composite Horizontal Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(CircleShape)
                .background(BorderSubtle)
        ) {
            Box(
                modifier = Modifier
                    .weight(outWeight)
                    .fillMaxHeight()
                    .background(SurfaceDark)
            )
            Box(
                modifier = Modifier
                    .weight(inWeight)
                    .fillMaxHeight()
                    .background(PastelHoneydew)
            )
            Box(
                modifier = Modifier
                    .weight(missedWeight)
                    .fillMaxHeight()
                    .background(PastelVanilla)
            )
            Box(
                modifier = Modifier
                    .weight(rejWeight)
                    .fillMaxHeight()
                    .background(PastelAliceBlue)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4-Column Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendItem(
                color = SurfaceDark,
                value = analytics.outgoingCalls.toString(),
                label = "Outgoing"
            )
            LegendItem(
                color = PastelHoneydew,
                value = analytics.incomingCalls.toString(),
                label = "Incoming"
            )
            LegendItem(
                color = PastelVanilla,
                value = analytics.missedCalls.toString(),
                label = "Missed"
            )
            LegendItem(
                color = PastelAliceBlue,
                value = analytics.rejectedCalls.toString(),
                label = "Rejected"
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    value: String,
    label: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(0.5.dp, BorderSubtle, CircleShape)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SurfaceDark
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted
        )
    }
}
