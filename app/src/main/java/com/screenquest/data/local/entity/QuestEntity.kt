package com.screenquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: String,              // DAILY, WEEKLY, BOSS
    val targetMinutes: Int,        // goal to achieve
    val xpReward: Int,
    val assignedDateEpochDay: Long,
    val isCompleted: Boolean = false,
    val completedAtEpochDay: Long? = null
)