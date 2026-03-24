package com.screenquest.domain.engine

object PetEngine {

    private const val MAX_HP = 100
    private const val MIN_HP = 0

    // HP gained when user meets daily goal
    private const val HP_GAIN_GOAL_MET = 10

    // HP gained per streak milestone
    private const val HP_GAIN_STREAK_BONUS = 5

    // HP lost per 30 minutes over the daily limit
    private const val HP_LOSS_PER_30_MIN_OVERAGE = 8

    // HP lost for breaking streak
    private const val HP_LOSS_STREAK_BROKEN = 15

    fun calculateNewHP(
        currentHP: Int,
        goalMet: Boolean,
        overageMinutes: Int,
        streakEvent: StreakEvent
    ): Int {
        var hp = currentHP

        if (goalMet) {
            hp += HP_GAIN_GOAL_MET
            if (streakEvent == StreakEvent.STREAK_EXTENDED) {
                hp += HP_GAIN_STREAK_BONUS
            }
        } else {
            val penalty = (overageMinutes / 30) * HP_LOSS_PER_30_MIN_OVERAGE
            hp -= penalty.coerceAtLeast(HP_LOSS_PER_30_MIN_OVERAGE)
        }

        if (streakEvent == StreakEvent.STREAK_BROKEN) {
            hp -= HP_LOSS_STREAK_BROKEN
        }

        return hp.coerceIn(MIN_HP, MAX_HP)
    }

    fun recoverHP(currentHP: Int, focusMinutes: Int): Int {
        // 1 HP per 10 focus minutes, capped at 20 HP per session
        val recovery = (focusMinutes / 10).coerceAtMost(20)
        return (currentHP + recovery).coerceAtMost(MAX_HP)
    }
}