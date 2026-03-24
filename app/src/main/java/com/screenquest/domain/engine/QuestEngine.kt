package com.screenquest.domain.engine

import com.screenquest.domain.model.Quest
import com.screenquest.domain.model.QuestDifficulty
import com.screenquest.domain.model.QuestType
import java.time.LocalDate

object QuestEngine {

    fun generateDailyQuests(
        avgDailyUsageMinutes: Int,
        currentStreak: Int,
        today: LocalDate = LocalDate.now()
    ): List<Quest> {
        return listOf(
            generateMainQuest(avgDailyUsageMinutes, today),
            generateBonusQuest(currentStreak, today)
        )
    }

    fun generateWeeklyQuest(
        avgDailyUsageMinutes: Int,
        today: LocalDate = LocalDate.now()
    ): Quest {
        val target = (avgDailyUsageMinutes * 0.6).toInt().coerceAtLeast(30)
        return Quest(
            title = "Weekly Discipline",
            description = "Stay under $target min/day for 5 out of 7 days this week",
            type = QuestType.WEEKLY,
            difficulty = QuestDifficulty.HARD,
            targetMinutes = target,
            xpReward = 300,
            assignedDate = today
        )
    }

    fun generateBossQuest(today: LocalDate = LocalDate.now()): Quest {
        return Quest(
            title = "The Silent Hour",
            description = "Do not open any tracked app for 60 consecutive minutes",
            type = QuestType.BOSS,
            difficulty = QuestDifficulty.HARD,
            targetMinutes = 60,
            xpReward = 500,
            assignedDate = today
        )
    }

    fun evaluateQuestCompletion(
        quest: Quest,
        actualMinutes: Int
    ): Boolean {
        return actualMinutes <= quest.targetMinutes
    }

    // --- Private helpers ---

    private fun generateMainQuest(
        avgMinutes: Int,
        today: LocalDate
    ): Quest {
        // Target is 20% less than current average, floored at 30 min
        val target = (avgMinutes * 0.8).toInt().coerceAtLeast(30)
        val (difficulty, xp) = difficultyFor(avgMinutes)
        return Quest(
            title = "Beat Your Average",
            description = "Keep total screen time under $target minutes today",
            type = QuestType.DAILY,
            difficulty = difficulty,
            targetMinutes = target,
            xpReward = xp,
            assignedDate = today
        )
    }

    private fun generateBonusQuest(
        streak: Int,
        today: LocalDate
    ): Quest {
        return when {
            streak < 3 -> Quest(
                title = "Early Riser",
                description = "Don't touch your phone for the first 30 minutes after waking",
                type = QuestType.DAILY,
                difficulty = QuestDifficulty.EASY,
                targetMinutes = 30,
                xpReward = 50,
                assignedDate = today
            )
            streak < 7 -> Quest(
                title = "Lunch Break Hero",
                description = "Stay off all apps during a 45-minute midday window",
                type = QuestType.DAILY,
                difficulty = QuestDifficulty.MEDIUM,
                targetMinutes = 45,
                xpReward = 80,
                assignedDate = today
            )
            else -> Quest(
                title = "Night Guard",
                description = "No phone usage after 9PM tonight",
                type = QuestType.DAILY,
                difficulty = QuestDifficulty.HARD,
                targetMinutes = 0,
                xpReward = 120,
                assignedDate = today
            )
        }
    }

    private fun difficultyFor(avgMinutes: Int): Pair<QuestDifficulty, Int> {
        return when {
            avgMinutes > 240 -> Pair(QuestDifficulty.EASY, 60)
            avgMinutes > 120 -> Pair(QuestDifficulty.MEDIUM, 90)
            else             -> Pair(QuestDifficulty.HARD, 130)
        }
    }
}