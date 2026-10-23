package com.aswin.callerinfo.data.repository

import com.aswin.callerinfo.data.model.ActivityPoint
import com.aswin.callerinfo.data.model.ContactAnalytics
import com.aswin.callerinfo.data.model.DashboardAnalytics

object MockCallDataProvider {

    fun getSampleDashboard(): DashboardAnalytics {
        val totalSecs = 142500L // ~39.5 hours
        val contacts = listOf(
            ContactAnalytics(
                name = "Miranda Lawson",
                number = "+1 (555) 234-5678",
                totalDurationSeconds = 38400L, // ~10.6 hrs
                totalCalls = 142,
                incomingCount = 82,
                outgoingCount = 52,
                missedCount = 8,
                incomingDurationSeconds = 22400L,
                outgoingDurationSeconds = 16000L,
                lastCallTimestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                percentageOfTotalDuration = 0.27f
            ),
            ContactAnalytics(
                name = "Marcus Vance",
                number = "+1 (555) 876-5432",
                totalDurationSeconds = 27800L, // ~7.7 hrs
                totalCalls = 96,
                incomingCount = 45,
                outgoingCount = 48,
                missedCount = 3,
                incomingDurationSeconds = 13800L,
                outgoingDurationSeconds = 14000L,
                lastCallTimestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                percentageOfTotalDuration = 0.195f
            ),
            ContactAnalytics(
                name = "Elena Rostova",
                number = "+1 (555) 432-1098",
                totalDurationSeconds = 21600L, // 6 hrs
                totalCalls = 68,
                incomingCount = 30,
                outgoingCount = 35,
                missedCount = 3,
                incomingDurationSeconds = 10000L,
                outgoingDurationSeconds = 11600L,
                lastCallTimestamp = System.currentTimeMillis() - 1000 * 60 * 360,
                percentageOfTotalDuration = 0.151f
            ),
            ContactAnalytics(
                name = "David Chen",
                number = "+1 (555) 345-6789",
                totalDurationSeconds = 17200L, // ~4.8 hrs
                totalCalls = 54,
                incomingCount = 28,
                outgoingCount = 22,
                missedCount = 4,
                incomingDurationSeconds = 9200L,
                outgoingDurationSeconds = 8000L,
                lastCallTimestamp = System.currentTimeMillis() - 1000 * 60 * 720,
                percentageOfTotalDuration = 0.12f
            ),
            ContactAnalytics(
                name = "Sophia Miller",
                number = "+1 (555) 901-2345",
                totalDurationSeconds = 13500L, // ~3.75 hrs
                totalCalls = 41,
                incomingCount = 18,
                outgoingCount = 20,
                missedCount = 3,
                incomingDurationSeconds = 6500L,
                outgoingDurationSeconds = 7000L,
                lastCallTimestamp = System.currentTimeMillis() - 1000 * 60 * 1440,
                percentageOfTotalDuration = 0.095f
            ),
            ContactAnalytics(
                name = "Liam Gallagher",
                number = "+1 (555) 678-9012",
                totalDurationSeconds = 9800L, // ~2.7 hrs
                totalCalls = 32,
                incomingCount = 14,
                outgoingCount = 16,
                missedCount = 2,
                incomingDurationSeconds = 4400L,
                outgoingDurationSeconds = 5400L,
                lastCallTimestamp = System.currentTimeMillis() - 1000 * 60 * 2880,
                percentageOfTotalDuration = 0.068f
            ),
            ContactAnalytics(
                name = "Aria Montgomery",
                number = "+1 (555) 789-0123",
                totalDurationSeconds = 7600L,
                totalCalls = 25,
                incomingCount = 11,
                outgoingCount = 12,
                missedCount = 2,
                incomingDurationSeconds = 3400L,
                outgoingDurationSeconds = 4200L,
                lastCallTimestamp = System.currentTimeMillis() - 1000 * 60 * 4320,
                percentageOfTotalDuration = 0.053f
            ),
            ContactAnalytics(
                name = "Oliver Queen",
                number = "+1 (555) 890-1234",
                totalDurationSeconds = 6600L,
                totalCalls = 19,
                incomingCount = 8,
                outgoingCount = 9,
                missedCount = 2,
                incomingDurationSeconds = 3000L,
                outgoingDurationSeconds = 3600L,
                lastCallTimestamp = System.currentTimeMillis() - 1000 * 60 * 5760,
                percentageOfTotalDuration = 0.046f
            )
        )

        val activity = listOf(
            ActivityPoint("Jan", 18200L, 56),
            ActivityPoint("Feb", 21400L, 72),
            ActivityPoint("Mar", 28900L, 95),
            ActivityPoint("Apr", 24300L, 81),
            ActivityPoint("May", 31200L, 108),
            ActivityPoint("Jun", 18500L, 65)
        )

        return DashboardAnalytics(
            totalDurationSeconds = totalSecs,
            totalCalls = 477,
            incomingCalls = 238,
            outgoingCalls = 214,
            missedCalls = 25,
            rejectedCalls = 12,
            averageDurationSeconds = totalSecs / 477,
            contactsCount = contacts.size,
            topContacts = contacts,
            monthlyActivity = activity
        )
    }
}
