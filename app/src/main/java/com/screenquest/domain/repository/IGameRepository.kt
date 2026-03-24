package com.screenquest.domain.repository

import com.screenquest.domain.model.Player
import com.screenquest.domain.model.PetState
import com.screenquest.domain.model.Quest
import kotlinx.coroutines.flow.Flow

interface IGameRepository {
    suspend fun initProfileIfMissing(nickname: String = "Player")
    suspend fun evaluateDay(totalMinutesToday: Int, dailyGoalMinutes: Int)
    suspend fun generateDailyQuestsIfMissing(avgDailyMinutes: Int)
    suspend fun completeQuest(questId: Long)
    fun observePlayer(): Flow<Player?>
    fun observePetState(): Flow<PetState?>
    fun observeActiveQuests(): Flow<List<Quest>>
    fun observeCompletedQuests(): Flow<List<Quest>>
}