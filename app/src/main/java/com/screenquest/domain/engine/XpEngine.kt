package com.screenquest.domain.engine


object XPEngine {

    // Base XP per minute spent off tracked/blocked apps
    private const val XP_PER_FOCUS_MINUTE = 2

    // Bonus multipliers
    private const val STREAK_BONUS_PER_DAY = 0.05f   // +5% per streak day, max 100%
    private const val MAX_STREAK_MULTIPLIER = 2.0f

    // Penalty
    private const val XP_PENALTY_PER_OVERAGE_MINUTE = 3

    fun calculateFocusXP(
        focusMinutes: Int,
        currentStreak: Int
    ): Int {
        val base = focusMinutes * XP_PER_FOCUS_MINUTE
        val multiplier = (1f + (currentStreak * STREAK_BONUS_PER_DAY))
            .coerceAtMost(MAX_STREAK_MULTIPLIER)
        return (base * multiplier).toInt()
    }

    fun calculatePenaltyXP(overageMinutes: Int): Int {
        return overageMinutes * XP_PENALTY_PER_OVERAGE_MINUTE
    }

    fun calculateQuestXP(baseReward: Int, streak: Int): Int {
        val bonus = (streak / 7) * 10   // +10 XP per week of streak
        return baseReward + bonus
    }

    fun calculateDailyLoginXP(streak: Int): Int {
        return when {
            streak >= 30 -> 50
            streak >= 14 -> 30
            streak >= 7  -> 20
            streak >= 3  -> 10
            else         -> 5
        }
    }
}