package com.screenquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_records")
data class UsageRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val dateEpochDay: Long,        // LocalDate.toEpochDay()
    val totalMinutesUsed: Int,
    val launchCount: Int,
    val xpEarned: Int = 0
)
