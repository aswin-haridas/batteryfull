package com.aswin.callerinfo.data.model

enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED,
    REJECTED,
    BLOCKED,
    UNKNOWN
}

data class CallRecord(
    val id: String,
    val number: String,
    val name: String?,
    val type: CallType,
    val timestamp: Long,
    val durationSeconds: Long
)
