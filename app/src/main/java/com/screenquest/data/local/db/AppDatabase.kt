package com.screenquest.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.screenquest.data.local.dao.AppLimitDao
import com.screenquest.data.local.dao.GameDao
import com.screenquest.data.local.dao.UsageDao
import com.screenquest.data.local.entity.AppLimitConfig
import com.screenquest.data.local.entity.PlayerProfile
import com.screenquest.data.local.entity.QuestEntity
import com.screenquest.data.local.entity.UsageRecord

@Database(
    entities = [
        UsageRecord::class,
        PlayerProfile::class,
        QuestEntity::class,
        AppLimitConfig::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usageDao(): UsageDao
    abstract fun gameDao(): GameDao
    abstract fun appLimitDao(): AppLimitDao
}