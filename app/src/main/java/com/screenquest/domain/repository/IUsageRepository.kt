package com.screenquest.domain.repository

import com.screenquest.domain.model.AppUsage
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface IUsageRepository {
    suspend fun syncToday()
    suspend fun pruneOldRecords()
    fun hasUsagePermission(): Boolean
    fun observeTodayUsage(): Flow<List<AppUsage>>
    fun observeTotalMinutesToday(): Flow<Int>
    fun observeRangeUsage(from: LocalDate, to: LocalDate): Flow<List<AppUsage>>
}