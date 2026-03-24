package com.screenquest.domain.usecase

import com.screenquest.domain.repository.IGameRepository
import com.screenquest.domain.repository.IUsageRepository
import javax.inject.Inject

class SyncAndEvaluateDayUseCase @Inject constructor(
    private val usageRepository: IUsageRepository,
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(dailyGoalMinutes: Int) {
        // 1. Pull latest from Android OS into Room
        usageRepository.syncToday()

        // 2. Get today's total
        val totalMinutes = usageRepository.getTotalMinutesToday()

        // 3. Run game evaluation
        gameRepository.evaluateDay(
            totalMinutesToday = totalMinutes,
            dailyGoalMinutes = dailyGoalMinutes
        )

        // 4. Generate quests based on recent average
        val avgMinutes = usageRepository.getAverageDailyMinutes(days = 7)
        gameRepository.generateDailyQuestsIfMissing(avgMinutes)

        // 5. Clean up old records quietly
        usageRepository.pruneOldRecords()
    }
}