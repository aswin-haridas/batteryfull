package com.aswin.callerinfo

import com.aswin.callerinfo.data.model.ContactAnalytics
import org.junit.Assert.assertEquals
import org.junit.Test

class ContactAnalyticsTest {

    @Test
    fun testDurationFormatting() {
        assertEquals("45s", ContactAnalytics.formatDuration(45))
        assertEquals("5m 30s", ContactAnalytics.formatDuration(330))
        assertEquals("2h 15m", ContactAnalytics.formatDuration(8100))
    }

    @Test
    fun testDisplayNameFallback() {
        val contactWithName = ContactAnalytics(
            name = "John Doe",
            number = "+1234567890",
            totalDurationSeconds = 120,
            totalCalls = 2,
            incomingCount = 1,
            outgoingCount = 1,
            missedCount = 0,
            incomingDurationSeconds = 60,
            outgoingDurationSeconds = 60,
            lastCallTimestamp = 1000L
        )
        assertEquals("John Doe", contactWithName.displayName)

        val contactWithoutName = ContactAnalytics(
            name = "",
            number = "+1234567890",
            totalDurationSeconds = 60,
            totalCalls = 1,
            incomingCount = 1,
            outgoingCount = 0,
            missedCount = 0,
            incomingDurationSeconds = 60,
            outgoingDurationSeconds = 0,
            lastCallTimestamp = 1000L
        )
        assertEquals("+1234567890", contactWithoutName.displayName)
    }
}
