package com.screenquest.domain.engine


import java.time.LocalDate

object StreakEngine {

    fun evaluateStreak(
        lastActiveEpochDay: Long,
        currentStreak: Int,
        today: LocalDate = LocalDate.now()
    ): StreakResult {
        val todayEpoch = today.toEpochDay()
        val daysDiff = todayEpoch - lastActiveEpochDay

        return when (daysDiff) {
            0L -> {
                // Already checked in today, no change
                StreakResult(
                    newStreak = currentStreak,
                    event = StreakEvent.ALREADY_ACTIVE_TODAY
                )
            }
            1L -> {
                // Consecutive day, increment streak
                StreakResult(
                    newStreak = currentStreak + 1,
                    event = StreakEvent.STREAK_EXTENDED
                )
            }
            else -> {
                // Missed one or more days, reset
                StreakResult(
                    newStreak = 1,
                    event = StreakEvent.STREAK_BROKEN
                )
            }
        }
    }

    fun isGoalMet(
        totalUsageMinutes: Int,
        dailyGoalMinutes: Int
    ): Boolean {
        return totalUsageMinutes <= dailyGoalMinutes
    }

    fun streakShieldShouldActivate(
        shieldsAvailable: Int,
        event: StreakEvent
    ): Boolean {
        return shieldsAvailable > 0 && event == StreakEvent.STREAK_BROKEN
    }
}

data class StreakResult(
    val newStreak: Int,
    val event: StreakEvent
)

enum class StreakEvent {
    STREAK_EXTENDED,
    STREAK_BROKEN,
    ALREADY_ACTIVE_TODAY
}