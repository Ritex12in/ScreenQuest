package com.screenquest.data.local.dao

import androidx.room.*
import com.screenquest.data.local.entity.PlayerProfile
import com.screenquest.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // --- Player ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: PlayerProfile)

    @Update
    suspend fun updateProfile(profile: PlayerProfile)

    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun observeProfile(): Flow<PlayerProfile?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getProfile(): PlayerProfile?

    @Query("UPDATE player_profile SET totalXP = totalXP + :xp WHERE id = 1")
    suspend fun addXP(xp: Int)

    @Query("""
        UPDATE player_profile 
        SET currentStreak = :streak, 
            longestStreak = MAX(longestStreak, :streak),
            lastActiveEpochDay = :epochDay
        WHERE id = 1
    """)
    suspend fun updateStreak(streak: Int, epochDay: Long)

    @Query("UPDATE player_profile SET petHealthPoints = :hp WHERE id = 1")
    suspend fun updatePetHealth(hp: Int)

    // --- Quests ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    @Query("SELECT * FROM quests WHERE assignedDateEpochDay = :epochDay")
    fun getQuestsForDay(epochDay: Long): List<QuestEntity>

    @Query("SELECT * FROM quests WHERE id = :questId")
    suspend fun getQuestById(questId: Long): QuestEntity?

    @Query("SELECT * FROM quests WHERE isCompleted = 0 ORDER BY assignedDateEpochDay DESC")
    fun getActiveQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE isCompleted = 1 ORDER BY completedAtEpochDay DESC LIMIT :limit")
    fun getCompletedQuests(limit: Int = 20): Flow<List<QuestEntity>>

    @Query("UPDATE quests SET isCompleted = 1, completedAtEpochDay = :epochDay WHERE id = :questId")
    suspend fun completeQuest(questId: Long, epochDay: Long)

}