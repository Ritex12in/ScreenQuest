package com.screenquest.data.local.dao

import androidx.room.*
import com.screenquest.data.local.entity.AppLimitConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface AppLimitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: AppLimitConfig)

    @Delete
    suspend fun delete(config: AppLimitConfig)

    @Query("SELECT * FROM app_limit_configs")
    fun observeAll(): Flow<List<AppLimitConfig>>

    @Query("SELECT * FROM app_limit_configs WHERE packageName = :packageName")
    suspend fun getConfig(packageName: String): AppLimitConfig?

    @Query("UPDATE app_limit_configs SET isBlocked = :blocked WHERE packageName = :packageName")
    suspend fun setBlocked(packageName: String, blocked: Boolean)
}