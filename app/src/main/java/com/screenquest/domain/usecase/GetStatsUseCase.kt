package com.screenquest.domain.usecase


import com.screenquest.domain.engine.InsightEngine
import com.screenquest.domain.model.AppUsage
import com.screenquest.domain.repository.IUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

data class StatsData(
    val weeklyUsage: List<AppUsage>,
    val averageDailyMinutes: Int,
    val topApps: List<AppUsage>,
    val improvementPercent: Int,
    val highRiskHours: List<Int>,
    val suggestionMessage: String
)

class GetStatsUseCase @Inject constructor(
    private val usageRepository: IUsageRepository,
    private val gameRepository: com.screenquest.domain.repository.IGameRepository
) {
    operator fun invoke(): Flow<StatsData> {
        val today = LocalDate.now()
        val sevenDaysAgo = today.minusDays(7)
        val fourteenDaysAgo = today.minusDays(14)

        return combine(
            usageRepository.observeRangeUsage(sevenDaysAgo, today),
            usageRepository.observeRangeUsage(fourteenDaysAgo, sevenDaysAgo),
            gameRepository.observePlayer()
        ) { thisWeek, lastWeek, player ->
            val thisAvg = InsightEngine.computeAverage(
                thisWeek.map { it.totalMinutesUsed }
            )
            val lastAvg = InsightEngine.computeAverage(
                lastWeek.map { it.totalMinutesUsed }
            )
            StatsData(
                weeklyUsage = thisWeek,
                averageDailyMinutes = thisAvg,
                topApps = InsightEngine.topApps(thisWeek),
                improvementPercent = InsightEngine.computeImprovementPercent(
                    thisWeekAvg = thisAvg,
                    lastWeekAvg = lastAvg
                ),
                highRiskHours = emptyList(), // requires hourly data — future feature
                suggestionMessage = InsightEngine.suggestionMessage(
                    averageMinutes = thisAvg,
                    currentStreak = player?.currentStreak ?: 0,
                    petHP = player?.petHealthPoints ?: 100
                )
            )
        }
    }
}