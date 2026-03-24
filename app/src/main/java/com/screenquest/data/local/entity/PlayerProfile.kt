package com.screenquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfile(
    @PrimaryKey
    val id: Int = 1,               // single row, always id = 1
    val nickname: String,
    val totalXP: Int = 0,
    val currentLevel: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveEpochDay: Long = 0,
    val petHealthPoints: Int = 100,// 0 to 100
    val totalFocusMinutes: Int = 0,
    val createdAtEpochDay: Long = 0
)
