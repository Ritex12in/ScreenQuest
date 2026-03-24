package com.screenquest.domain.model

data class AppUsage(
    val packageName: String,
    val appName: String,
    val totalMinutesUsed: Int,
    val launchCount: Int,
    val limitMinutes: Int = 0,       // 0 means no limit set
    val dateEpochDay: Long = 0L,
    val isLimitExceeded: Boolean = false
) {
    val usageFraction: Float
        get() = if (limitMinutes > 0)
            (totalMinutesUsed.toFloat() / limitMinutes.toFloat()).coerceAtMost(1f)
        else 0f
}