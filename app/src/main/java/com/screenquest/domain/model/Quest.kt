package com.screenquest.domain.model

import java.time.LocalDate

enum class QuestType { DAILY, WEEKLY, BOSS }

enum class QuestDifficulty { EASY, MEDIUM, HARD }

data class Quest(
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: QuestType,
    val difficulty: QuestDifficulty,
    val targetMinutes: Int,
    val xpReward: Int,
    val assignedDate: LocalDate,
    val isCompleted: Boolean = false,
    val completedDate: LocalDate? = null
)