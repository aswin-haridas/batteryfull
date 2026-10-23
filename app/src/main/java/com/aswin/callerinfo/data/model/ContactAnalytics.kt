package com.aswin.callerinfo.data.model

data class ContactAnalytics(
    val name: String,
    val number: String,
    val totalDurationSeconds: Long,
    val totalCalls: Int,
    val incomingCount: Int,
    val outgoingCount: Int,
    val missedCount: Int,
    val incomingDurationSeconds: Long,
    val outgoingDurationSeconds: Long,
    val lastCallTimestamp: Long,
    val percentageOfTotalDuration: Float = 0f
) {
    val displayName: String
        get() = name.ifBlank { number }

    val formattedTotalDuration: String
        get() = formatDuration(totalDurationSeconds)

    companion object {
        fun formatDuration(seconds: Long): String {
            val hrs = seconds / 3600
            val mins = (seconds % 3600) / 60
            val secs = seconds % 60
            return when {
                hrs > 0 -> "${hrs}h ${mins}m"
                mins > 0 -> "${mins}m ${secs}s"
                else -> "${secs}s"
            }
        }
    }
}

data class DashboardAnalytics(
    val totalDurationSeconds: Long = 0,
    val totalCalls: Int = 0,
    val incomingCalls: Int = 0,
    val outgoingCalls: Int = 0,
    val missedCalls: Int = 0,
    val rejectedCalls: Int = 0,
    val averageDurationSeconds: Long = 0,
    val contactsCount: Int = 0,
    val topContacts: List<ContactAnalytics> = emptyList(),
    val monthlyActivity: List<ActivityPoint> = emptyList()
) {
    val formattedTotalDuration: String
        get() = ContactAnalytics.formatDuration(totalDurationSeconds)

    val formattedAvgDuration: String
        get() = ContactAnalytics.formatDuration(averageDurationSeconds)
}

data class ActivityPoint(
    val label: String,
    val totalSeconds: Long,
    val callCount: Int
)
