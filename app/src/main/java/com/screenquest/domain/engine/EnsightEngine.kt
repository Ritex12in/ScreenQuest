package com.screenquest.domain.engine

import com.screenquest.domain.model.AppUsage

object InsightEngine {

    data class WeeklyInsight(
        val averageDailyMinutes: Int,
        val bestDay: String,
        val worstDay: String,
        val mostUsedApp: String,
        val totalFocusMinutes: Int,
        val improvementPercent: Int   // vs previous week, negative = worse
    )

    fun computeAverage(dailyMinutes: List<Int>): Int {
        if (dailyMinutes.isEmpty()) return 0
        return dailyMinutes.sum() / dailyMinutes.size
    }

    fun computeImprovementPercent(
        thisWeekAvg: Int,
        lastWeekAvg: Int
    ): Int {
        if (lastWeekAvg == 0) return 0
        return ((lastWeekAvg - thisWeekAvg).toFloat() / lastWeekAvg * 100).toInt()
    }

    fun detectHighRiskHours(
        hourlyUsageMap: Map<Int, Int>   // hour (0-23) -> minutes used
    ): List<Int> {
        if (hourlyUsageMap.isEmpty()) return emptyList()
        val average = hourlyUsageMap.values.average()
        return hourlyUsageMap
            .filter { (_, minutes) -> minutes > average * 1.5 }
            .keys
            .sorted()
    }

    fun topApps(usageList: List<AppUsage>, count: Int = 3): List<AppUsage> {
        return usageList.sortedByDescending { it.totalMinutesUsed }.take(count)
    }

    fun suggestionMessage(
        averageMinutes: Int,
        currentStreak: Int,
        petHP: Int
    ): String {
        return when {
            petHP < 20 ->
                "Your companion is struggling. A focused hour could turn things around."
            currentStreak == 0 ->
                "Start small. Even one good day builds momentum."
            averageMinutes > 240 ->
                "You are averaging over 4 hours. Try cutting 30 minutes tomorrow."
            averageMinutes < 60 ->
                "Outstanding discipline. Keep protecting your attention."
            else ->
                "Streak of $currentStreak days. Stay consistent."
        }
    }
}