package com.aswin.callerinfo.data.repository

import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.aswin.callerinfo.data.model.ActivityPoint
import com.aswin.callerinfo.data.model.CallRecord
import com.aswin.callerinfo.data.model.CallType
import com.aswin.callerinfo.data.model.ContactAnalytics
import com.aswin.callerinfo.data.model.DashboardAnalytics
import com.aswin.callerinfo.data.model.TimeFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CallLogRepository(private val context: Context) {

    private val nameCache = mutableMapOf<String, String>()

    suspend fun getAnalytics(filter: TimeFilter): DashboardAnalytics = withContext(Dispatchers.IO) {
        val records = fetchCallRecords(filter)
        if (records.isEmpty()) {
            return@withContext DashboardAnalytics()
        }
        computeDashboard(records)
    }

    private fun fetchCallRecords(filter: TimeFilter): List<CallRecord> {
        val startTime = calculateStartTime(filter)
        val records = mutableListOf<CallRecord>()

        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )

        val selection = if (startTime > 0) "${CallLog.Calls.DATE} >= ?" else null
        val selectionArgs = if (startTime > 0) arrayOf(startTime.toString()) else null
        val sortOrder = "${CallLog.Calls.DATE} DESC"

        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.use {
                val idIdx = it.getColumnIndex(CallLog.Calls._ID)
                val numberIdx = it.getColumnIndex(CallLog.Calls.NUMBER)
                val nameIdx = it.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val typeIdx = it.getColumnIndex(CallLog.Calls.TYPE)
                val dateIdx = it.getColumnIndex(CallLog.Calls.DATE)
                val durationIdx = it.getColumnIndex(CallLog.Calls.DURATION)

                while (it.moveToNext()) {
                    val id = if (idIdx >= 0) it.getString(idIdx) ?: "" else ""
                    val rawNumber = if (numberIdx >= 0) it.getString(numberIdx) ?: "Unknown" else "Unknown"
                    val number = normalizeNumber(rawNumber)
                    var name = if (nameIdx >= 0) it.getString(nameIdx) else null

                    if (name.isNullOrBlank()) {
                        name = resolveContactName(number)
                    }

                    val typeInt = if (typeIdx >= 0) it.getInt(typeIdx) else 0
                    val type = when (typeInt) {
                        CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
                        CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
                        CallLog.Calls.MISSED_TYPE -> CallType.MISSED
                        CallLog.Calls.REJECTED_TYPE -> CallType.REJECTED
                        CallLog.Calls.BLOCKED_TYPE -> CallType.BLOCKED
                        else -> CallType.UNKNOWN
                    }

                    val date = if (dateIdx >= 0) it.getLong(dateIdx) else 0L
                    val duration = if (durationIdx >= 0) it.getLong(durationIdx) else 0L

                    records.add(
                        CallRecord(
                            id = id,
                            number = number,
                            name = name,
                            type = type,
                            timestamp = date,
                            durationSeconds = duration
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted or revoked
            return emptyList()
        } catch (e: Exception) {
            return emptyList()
        }

        return records
    }

    private fun resolveContactName(phoneNumber: String): String? {
        if (phoneNumber.isBlank() || phoneNumber == "Unknown") return null
        if (nameCache.containsKey(phoneNumber)) return nameCache[phoneNumber]

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        try {
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIdx = it.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIdx >= 0) {
                        val name = it.getString(nameIdx)
                        if (!name.isNullOrBlank()) {
                            nameCache[phoneNumber] = name
                            return name
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        nameCache[phoneNumber] = phoneNumber
        return null
    }

    private fun normalizeNumber(raw: String): String {
        return raw.replace("\\s+".toRegex(), "").replace("-", "")
    }

    private fun calculateStartTime(filter: TimeFilter): Long {
        val cal = Calendar.getInstance()
        return when (filter) {
            TimeFilter.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            TimeFilter.THIS_WEEK -> {
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            TimeFilter.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            TimeFilter.ALL_TIME -> 0L
        }
    }

    private fun computeDashboard(records: List<CallRecord>): DashboardAnalytics {
        var totalSecs = 0L
        var incomingCount = 0
        var outgoingCount = 0
        var missedCount = 0
        var rejectedCount = 0

        val contactGroups = mutableMapOf<String, MutableList<CallRecord>>()

        for (record in records) {
            totalSecs += record.durationSeconds
            when (record.type) {
                CallType.INCOMING -> incomingCount++
                CallType.OUTGOING -> outgoingCount++
                CallType.MISSED -> missedCount++
                CallType.REJECTED -> rejectedCount++
                else -> {}
            }

            val key = record.name?.takeIf { it.isNotBlank() } ?: record.number
            val list = contactGroups.getOrPut(key) { mutableListOf() }
            list.add(record)
        }

        val totalCalls = records.size
        val avgDuration = if (totalCalls > 0) totalSecs / totalCalls else 0L

        val contactAnalyticsList = contactGroups.map { (key, group) ->
            var contactDuration = 0L
            var inc = 0
            var out = 0
            var mis = 0
            var inDuration = 0L
            var outDuration = 0L
            var latestTime = 0L
            var resolvedName: String? = null
            var resolvedNum = key

            for (r in group) {
                contactDuration += r.durationSeconds
                if (r.timestamp > latestTime) latestTime = r.timestamp
                if (!r.name.isNullOrBlank()) resolvedName = r.name
                if (r.number.isNotBlank() && r.number != "Unknown") resolvedNum = r.number

                when (r.type) {
                    CallType.INCOMING -> {
                        inc++
                        inDuration += r.durationSeconds
                    }
                    CallType.OUTGOING -> {
                        out++
                        outDuration += r.durationSeconds
                    }
                    CallType.MISSED -> mis++
                    else -> {}
                }
            }

            val percentage = if (totalSecs > 0) {
                (contactDuration.toFloat() / totalSecs.toFloat())
            } else 0f

            ContactAnalytics(
                name = resolvedName ?: key,
                number = resolvedNum,
                totalDurationSeconds = contactDuration,
                totalCalls = group.size,
                incomingCount = inc,
                outgoingCount = out,
                missedCount = mis,
                incomingDurationSeconds = inDuration,
                outgoingDurationSeconds = outDuration,
                lastCallTimestamp = latestTime,
                percentageOfTotalDuration = percentage
            )
        }.sortedByDescending { it.totalDurationSeconds }

        val monthlyActivity = computeActivityPoints(records)

        return DashboardAnalytics(
            totalDurationSeconds = totalSecs,
            totalCalls = totalCalls,
            incomingCalls = incomingCount,
            outgoingCalls = outgoingCount,
            missedCalls = missedCount,
            rejectedCalls = rejectedCount,
            averageDurationSeconds = avgDuration,
            contactsCount = contactAnalyticsList.size,
            topContacts = contactAnalyticsList,
            monthlyActivity = monthlyActivity
        )
    }

    private fun computeActivityPoints(records: List<CallRecord>): List<ActivityPoint> {
        val dateFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val grouped = records.groupBy {
            dateFormat.format(Date(it.timestamp))
        }

        return grouped.map { (label, items) ->
            ActivityPoint(
                label = label,
                totalSeconds = items.sumOf { it.durationSeconds },
                callCount = items.size
            )
        }
    }
}
