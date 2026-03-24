package com.screenquest.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screenquest.domain.model.AppUsage
import com.screenquest.domain.usecase.GetStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StatsUiState(
    val averageDailyMinutes: Int = 0,
    val topApps: List<AppUsage> = emptyList(),
    val weeklyUsage: List<AppUsage> = emptyList(),
    val improvementPercent: Int = 0,
    val suggestionMessage: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    getStats: GetStatsUseCase
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = getStats()
        .map { data ->
            StatsUiState(
                averageDailyMinutes = data.averageDailyMinutes,
                topApps = data.topApps,
                weeklyUsage = data.weeklyUsage,
                improvementPercent = data.improvementPercent,
                suggestionMessage = data.suggestionMessage,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatsUiState()
        )
}