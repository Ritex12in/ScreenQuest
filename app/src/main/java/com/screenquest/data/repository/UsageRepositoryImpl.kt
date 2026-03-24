package com.screenquest.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import com.screenquest.data.local.dao.UsageDao
import com.screenquest.data.local.entity.UsageRecord
import com.screenquest.domain.model.AppUsage
import com.screenquest.domain.repository.IUsageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageDao: UsageDao
) : IUsageRepository {

    private val usageStatsManager = context.getSystemService(
        Context.USAGE_STATS_SERVICE
    ) as UsageStatsManager

    override suspend fun syncToday() {
        val today = LocalDate.now()
        val records = queryFromOs(today)
        records.forEach { usageDao.insertOrUpdate(it) }
    }

    override fun observeTodayUsage(): Flow<List<AppUsage>> {
        val todayEpoch = LocalDate.now().toEpochDay()
        return usageDao.getRecordsForDay(todayEpoch)
            .map { records -> records.map { it.toAppUsage() } }
    }

    override fun observeRangeUsage(from: LocalDate, to: LocalDate): Flow<List<AppUsage>> {
        return usageDao.getRecordsForRange(from.toEpochDay(), to.toEpochDay())
            .map { records -> records.map { it.toAppUsage() } }
    }

    override suspend fun getTotalMinutesToday(): Int {
        return observeTotalMinutesToday().first()
    }

    override suspend fun getAverageDailyMinutes(days: Int): Int {
        val from = LocalDate.now().minusDays(days.toLong())
        val to = LocalDate.now()
        val records = observeRangeUsage(from, to).first()
        return records.sumOf { it.totalMinutesUsed } / days
    }

    override fun observeTotalMinutesToday(): Flow<Int> {
        val todayEpoch = LocalDate.now().toEpochDay()
        return usageDao.getTotalMinutesForDay(todayEpoch).map { it ?: 0 }
    }

    override suspend fun pruneOldRecords() {
        val cutoff = LocalDate.now().minusDays(90).toEpochDay()
        usageDao.deleteOlderThan(cutoff)
    }

    override fun hasUsagePermission(): Boolean {
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            System.currentTimeMillis() - 10_000,
            System.currentTimeMillis()
        )
        return stats != null
    }

    // --- Private ---

    private fun queryFromOs(date: LocalDate): List<UsageRecord> {
        val zone = ZoneId.systemDefault()
        val startMs = date.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMs = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        return usageStatsManager
            .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startMs, endMs)
            .filter { it.totalTimeInForeground > 0 }
            .filter { !isSystemPackage(it.packageName) }
            .map { stat ->
                UsageRecord(
                    packageName = stat.packageName,
                    appName = resolveAppName(stat.packageName),
                    dateEpochDay = date.toEpochDay(),
                    totalMinutesUsed = (stat.totalTimeInForeground / 60_000).toInt(),
                    launchCount = 0
                )
            }
    }

    private fun resolveAppName(packageName: String): String {
        return try {
            val info = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(info).toString()
        } catch (_: Exception) {
            packageName
        }
    }

    private fun isSystemPackage(packageName: String): Boolean {
        return listOf("com.android", "android", "com.screenquest")
            .any { packageName.startsWith(it) }
    }

    private fun UsageRecord.toAppUsage() = AppUsage(
        packageName = packageName,
        appName = appName,
        totalMinutesUsed = totalMinutesUsed,
        launchCount = launchCount
    )
}