package com.aswin.callerinfo.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aswin.callerinfo.data.model.ContactAnalytics
import com.aswin.callerinfo.ui.theme.PastelAliceBlue
import com.aswin.callerinfo.ui.theme.PastelHoneydew
import com.aswin.callerinfo.ui.theme.PastelVanilla
import com.aswin.callerinfo.ui.theme.PrimaryLime
import com.aswin.callerinfo.ui.theme.SurfaceCard
import com.aswin.callerinfo.ui.theme.SurfaceDark
import com.aswin.callerinfo.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailSheet(
    contact: ContactAnalytics,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = contact.displayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = contact.number,
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SurfaceDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Stat Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PastelHoneydew)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Total Talk Time",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SurfaceDark.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = contact.formattedTotalDuration,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${contact.totalCalls} total conversations logged",
                        fontSize = 13.sp,
                        color = SurfaceDark.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breakdown Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Incoming
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PastelAliceBlue)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Incoming",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SurfaceDark.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${contact.incomingCount} calls",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceDark
                    )
                    Text(
                        text = ContactAnalytics.formatDuration(contact.incomingDurationSeconds),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                // Outgoing
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PastelVanilla)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Outgoing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SurfaceDark.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${contact.outgoingCount} calls",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceDark
                    )
                    Text(
                        text = ContactAnalytics.formatDuration(contact.outgoingDurationSeconds),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                // Missed
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceDark.copy(alpha = 0.05f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Missed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SurfaceDark.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${contact.missedCount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceDark
                    )
                    Text(
                        text = "Unanswered",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dial action button
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.number}"))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceDark,
                    contentColor = PrimaryLime
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Call ${contact.displayName}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
