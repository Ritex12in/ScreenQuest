package com.screenquest.data.local.dao


import androidx.room.*
import com.screenquest.data.local.entity.UsageRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: UsageRecord)

    @Query("SELECT * FROM usage_records WHERE dateEpochDay = :epochDay")
    fun getRecordsForDay(epochDay: Long): Flow<List<UsageRecord>>

    @Query("SELECT * FROM usage_records WHERE dateEpochDay BETWEEN :fromDay AND :toDay")
    fun getRecordsForRange(fromDay: Long, toDay: Long): Flow<List<UsageRecord>>

    @Query("SELECT * FROM usage_records WHERE packageName = :packageName ORDER BY dateEpochDay DESC")
    fun getRecordsForApp(packageName: String): Flow<List<UsageRecord>>

    @Query("""
        SELECT packageName, appName, SUM(totalMinutesUsed) as totalMinutesUsed, 
        SUM(launchCount) as launchCount, SUM(xpEarned) as xpEarned, 
        dateEpochDay, id
        FROM usage_records 
        WHERE dateEpochDay BETWEEN :fromDay AND :toDay
        GROUP BY packageName 
        ORDER BY totalMinutesUsed DESC
    """)
    fun getTopAppsByUsage(fromDay: Long, toDay: Long): Flow<List<UsageRecord>>

    @Query("SELECT SUM(totalMinutesUsed) FROM usage_records WHERE dateEpochDay = :epochDay")
    fun getTotalMinutesForDay(epochDay: Long): Flow<Int?>

    @Query("DELETE FROM usage_records WHERE dateEpochDay < :epochDay")
    suspend fun deleteOlderThan(epochDay: Long)
}