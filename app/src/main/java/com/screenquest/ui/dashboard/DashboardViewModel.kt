package com.screenquest.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screenquest.domain.model.AppUsage
import com.screenquest.domain.model.PetState
import com.screenquest.domain.model.Player
import com.screenquest.domain.usecase.GetDashboardDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DashboardUiState(
    val player: Player? = null,
    val petState: PetState? = null,
    val topApps: List<AppUsage> = emptyList(),
    val totalMinutesToday: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDashboardData: GetDashboardDataUseCase
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = getDashboardData()
        .map { data ->
            DashboardUiState(
                player = data.player,
                petState = data.petState,
                topApps = data.topApps,
                totalMinutesToday = data.totalMinutesToday,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState()
        )
}