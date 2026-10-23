package com.aswin.callerinfo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aswin.callerinfo.data.model.ContactAnalytics
import com.aswin.callerinfo.ui.theme.BorderSubtle
import com.aswin.callerinfo.ui.theme.PastelHoneydew
import com.aswin.callerinfo.ui.theme.SurfaceCard
import com.aswin.callerinfo.ui.theme.SurfaceDark
import com.aswin.callerinfo.ui.theme.TextMuted

@Composable
fun ContactCard(
    contact: ContactAnalytics,
    rank: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initials = contact.displayName.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()
        .ifBlank { "#" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Rank and Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (rank <= 3) SurfaceDark else SurfaceDark.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) SurfaceCard else SurfaceDark
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = contact.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurfaceDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${contact.totalCalls} calls • ${contact.incomingCount} in, ${contact.outgoingCount} out",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // Duration Value Badge
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = contact.formattedTotalDuration,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SurfaceDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            val percentInt = (contact.percentageOfTotalDuration * 100).toInt()
            if (percentInt > 0) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PastelHoneydew)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$percentInt% time",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SurfaceDark
                    )
                }
            }
        }
    }
}
