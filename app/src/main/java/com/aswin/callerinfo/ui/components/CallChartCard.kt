package com.aswin.callerinfo.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aswin.callerinfo.data.model.ActivityPoint
import com.aswin.callerinfo.ui.theme.BorderSubtle
import com.aswin.callerinfo.ui.theme.PastelVanilla
import com.aswin.callerinfo.ui.theme.PrimaryLime
import com.aswin.callerinfo.ui.theme.SurfaceCard
import com.aswin.callerinfo.ui.theme.SurfaceDark
import com.aswin.callerinfo.ui.theme.TextMuted

@Composable
fun CallChartCard(
    activity: List<ActivityPoint>,
    totalFormatted: String,
    modifier: Modifier = Modifier
) {
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
                    text = "Activity Trends",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurfaceDark
                )
                Text(
                    text = "Duration vs Call volume",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PastelVanilla)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = totalFormatted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceDark
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = "Talk Time", fontSize = 12.sp, color = TextMuted)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PrimaryLime)
                        .border(1.dp, SurfaceDark.copy(alpha = 0.2f), CircleShape)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = "Call Count", fontSize = 12.sp, color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Canvas Spline Chart
        val points = if (activity.isNotEmpty()) activity else listOf(
            ActivityPoint("Mon", 1200L, 4),
            ActivityPoint("Tue", 2400L, 8),
            ActivityPoint("Wed", 1800L, 6),
            ActivityPoint("Thu", 3600L, 12),
            ActivityPoint("Fri", 3000L, 10),
            ActivityPoint("Sat", 4200L, 15)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(105.dp)) {
                val width = size.width
                val height = size.height
                val count = points.size

                val maxSecs = (points.maxOfOrNull { it.totalSeconds } ?: 1L).coerceAtLeast(1L).toFloat()
                val maxCalls = (points.maxOfOrNull { it.callCount } ?: 1).coerceAtLeast(1).toFloat()

                val durationPath = Path()
                val callsPath = Path()

                points.forEachIndexed { index, pt ->
                    val x = if (count > 1) index * (width / (count - 1)) else width / 2
                    val ySec = height - (pt.totalSeconds.toFloat() / maxSecs * (height - 24f)) - 12f
                    val yCall = height - (pt.callCount.toFloat() / maxCalls * (height - 24f)) - 12f

                    if (index == 0) {
                        durationPath.moveTo(x, ySec)
                        callsPath.moveTo(x, yCall)
                    } else {
                        val prevX = (index - 1) * (width / (count - 1))
                        val prevSec = points[index - 1].totalSeconds.toFloat()
                        val prevYSec = height - (prevSec / maxSecs * (height - 24f)) - 12f
                        val cx = (prevX + x) / 2f
                        durationPath.cubicTo(cx, prevYSec, cx, ySec, x, ySec)

                        val prevCall = points[index - 1].callCount.toFloat()
                        val prevYCall = height - (prevCall / maxCalls * (height - 24f)) - 12f
                        callsPath.cubicTo(cx, prevYCall, cx, yCall, x, yCall)
                    }

                    // Draw node points
                    drawCircle(
                        color = SurfaceDark,
                        radius = 4.dp.toPx(),
                        center = Offset(x, ySec)
                    )
                }

                // Draw dashed calls path
                drawPath(
                    path = callsPath,
                    color = PrimaryLime,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                    )
                )

                // Draw solid duration path
                drawPath(
                    path = durationPath,
                    color = SurfaceDark,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // X-Axis Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEach { pt ->
                    Text(
                        text = pt.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
