package com.screenquest.domain.usecase

import com.screenquest.domain.model.AppUsage
import com.screenquest.domain.model.PetState
import com.screenquest.domain.model.Player
import com.screenquest.domain.repository.IGameRepository
import com.screenquest.domain.repository.IUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DashboardData(
    val player: Player?,
    val petState: PetState?,
    val topApps: List<AppUsage>,
    val totalMinutesToday: Int
)

class GetDashboardDataUseCase @Inject constructor(
    private val usageRepository: IUsageRepository,
    private val gameRepository: IGameRepository
) {
    operator fun invoke(): Flow<DashboardData> {
        return combine(
            gameRepository.observePlayer(),
            gameRepository.observePetState(),
            usageRepository.observeTodayUsage(),
            usageRepository.observeTotalMinutesToday()
        ) { player, petState, appUsages, totalMinutes ->
            DashboardData(
                player = player,
                petState = petState,
                topApps = appUsages.sortedByDescending { it.totalMinutesUsed }.take(5),
                totalMinutesToday = totalMinutes
            )
        }
    }
}