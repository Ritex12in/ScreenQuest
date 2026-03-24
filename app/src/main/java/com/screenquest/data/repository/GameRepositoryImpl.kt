package com.screenquest.data.repository

import com.screenquest.data.local.dao.GameDao
import com.screenquest.data.local.entity.PlayerProfile
import com.screenquest.data.local.entity.QuestEntity
import com.screenquest.domain.engine.PetEngine
import com.screenquest.domain.engine.QuestEngine
import com.screenquest.domain.engine.StreakEngine
import com.screenquest.domain.engine.XPEngine
import com.screenquest.domain.model.Player
import com.screenquest.domain.model.PetState
import com.screenquest.domain.model.Quest
import com.screenquest.domain.model.QuestDifficulty
import com.screenquest.domain.model.QuestType
import com.screenquest.domain.model.XPTable
import com.screenquest.domain.repository.IGameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao
) : IGameRepository {

    override suspend fun initProfileIfMissing(nickname: String) {
        if (gameDao.getProfile() != null) return
        gameDao.insertProfile(
            PlayerProfile(
                nickname = nickname,
                createdAtEpochDay = LocalDate.now().toEpochDay()
            )
        )
    }

    override fun observePlayer(): Flow<Player?> {
        return gameDao.observeProfile().map { it?.toPlayer() }
    }

    override fun observePetState(): Flow<PetState?> {
        return gameDao.observeProfile().map { profile ->
            profile?.let { PetState(it.petHealthPoints) }
        }
    }

    override suspend fun evaluateDay(totalMinutesToday: Int, dailyGoalMinutes: Int) {
        val profile = gameDao.getProfile() ?: return
        val today = LocalDate.now()
        val todayEpoch = today.toEpochDay()

        if (profile.lastActiveEpochDay == todayEpoch) return

        val goalMet = StreakEngine.isGoalMet(totalMinutesToday, dailyGoalMinutes)

        val streakResult = StreakEngine.evaluateStreak(
            lastActiveEpochDay = profile.lastActiveEpochDay,
            currentStreak = profile.currentStreak,
            today = today
        )

        val overageMinutes = (totalMinutesToday - dailyGoalMinutes).coerceAtLeast(0)

        val newHP = PetEngine.calculateNewHP(
            currentHP = profile.petHealthPoints,
            goalMet = goalMet,
            overageMinutes = overageMinutes,
            streakEvent = streakResult.event
        )

        val xpDelta = if (goalMet) {
            XPEngine.calculateFocusXP(
                focusMinutes = (dailyGoalMinutes - overageMinutes).coerceAtLeast(0),
                currentStreak = streakResult.newStreak
            ) + XPEngine.calculateDailyLoginXP(streakResult.newStreak)
        } else {
            -XPEngine.calculatePenaltyXP(overageMinutes)
        }

        gameDao.updateStreak(streakResult.newStreak, todayEpoch)
        gameDao.updatePetHealth(newHP)
        if (xpDelta > 0) gameDao.addXP(xpDelta)

        val updatedProfile = gameDao.getProfile() ?: return
        val newLevel = XPTable.levelForXP(updatedProfile.totalXP)
        if (newLevel != updatedProfile.currentLevel) {
            gameDao.updateProfile(updatedProfile.copy(currentLevel = newLevel))
        }
    }

    override suspend fun generateDailyQuestsIfMissing(avgDailyMinutes: Int) {
        withContext(Dispatchers.IO) {
            val today = LocalDate.now()
            val existing = gameDao.getQuestsForDay(today.toEpochDay())
            if (existing.isNotEmpty()) return@withContext

            val profile = gameDao.getProfile() ?: return@withContext
            QuestEngine.generateDailyQuests(
                avgDailyUsageMinutes = avgDailyMinutes,
                currentStreak = profile.currentStreak,
                today = today
            ).forEach { gameDao.insertQuest(it.toEntity()) }
        }
    }

    override suspend fun completeQuest(questId: Long) {
        val todayEpoch = LocalDate.now().toEpochDay()
        gameDao.completeQuest(questId, todayEpoch)

        val profile = gameDao.getProfile() ?: return
        val quest = gameDao.getQuestById(questId) ?: return
        val xp = XPEngine.calculateQuestXP(quest.xpReward, profile.currentStreak)
        gameDao.addXP(xp)
    }

    override fun observeActiveQuests(): Flow<List<Quest>> {
        return gameDao.getActiveQuests().map { list -> list.map { it.toQuest() } }
    }

    override fun observeCompletedQuests(): Flow<List<Quest>> {
        return gameDao.getCompletedQuests().map { list -> list.map { it.toQuest() } }
    }

    // --- Mappers ---

    private fun PlayerProfile.toPlayer() = Player(
        nickname = nickname,
        totalXP = totalXP,
        currentLevel = currentLevel,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastActiveEpochDay = lastActiveEpochDay,
        petHealthPoints = petHealthPoints,
        totalFocusMinutes = totalFocusMinutes
    )

    private fun Quest.toEntity() = QuestEntity(
        title = title,
        description = description,
        type = type.name,
        targetMinutes = targetMinutes,
        xpReward = xpReward,
        assignedDateEpochDay = assignedDate.toEpochDay()
    )

    private fun QuestEntity.toQuest() = Quest(
        id = id,
        title = title,
        description = description,
        type = QuestType.valueOf(type),
        difficulty = QuestDifficulty.MEDIUM,
        targetMinutes = targetMinutes,
        xpReward = xpReward,
        assignedDate = LocalDate.ofEpochDay(assignedDateEpochDay),
        isCompleted = isCompleted,
        completedDate = completedAtEpochDay?.let { LocalDate.ofEpochDay(it) }
    )
}