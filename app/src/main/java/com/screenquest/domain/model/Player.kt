package com.screenquest.domain.model

data class Player(
    val nickname: String,
    val totalXP: Int,
    val currentLevel: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveEpochDay: Long,
    val petHealthPoints: Int,
    val totalFocusMinutes: Int
) {
    val xpForCurrentLevel: Int get() = XPTable.xpRequiredForLevel(currentLevel)
    val xpForNextLevel: Int get() = XPTable.xpRequiredForLevel(currentLevel + 1)
    val xpProgress: Int get() = totalXP - xpForCurrentLevel
    val xpNeededToLevelUp: Int get() = xpForNextLevel - xpForCurrentLevel
    val levelProgressFraction: Float
        get() = xpProgress.toFloat() / xpNeededToLevelUp.toFloat()
}

object XPTable {
    // XP required to reach a given level (cumulative)
    fun xpRequiredForLevel(level: Int): Int {
        if (level <= 1) return 0
        // Quadratic curve: each level costs more than the last
        return (100 * (level - 1) * level) / 2
    }

    fun levelForXP(totalXP: Int): Int {
        var level = 1
        while (xpRequiredForLevel(level + 1) <= totalXP) {
            level++
        }
        return level
    }

    fun levelTitle(level: Int): String {
        return when (level) {
            1 -> "Phone Slave"
            2 -> "Distracted Wanderer"
            3 -> "Aware Beginner"
            4 -> "Focused Apprentice"
            5 -> "Habit Builder"
            6 -> "Screen Breaker"
            7 -> "Mindful Warrior"
            8 -> "Digital Stoic"
            9 -> "Focus Master"
            10 -> "Digital Monk"
            else -> if (level > 10) "Enlightened" else "Unknown"
        }
    }
}