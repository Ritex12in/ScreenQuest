package com.screenquest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screenquest.domain.usecase.InitPlayerUseCase
import com.screenquest.domain.usecase.InitResult
import com.screenquest.domain.usecase.SyncAndEvaluateDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainState(
    val permissionRequired: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val initPlayer: InitPlayerUseCase,
    private val syncAndEvaluateDay: SyncAndEvaluateDayUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun onAppOpen() {
        viewModelScope.launch {
            when (initPlayer()) {
                is InitResult.PermissionRequired -> {
                    _state.update { it.copy(permissionRequired = true, isLoading = false) }
                }
                is InitResult.Ready -> {
                    _state.update { it.copy(permissionRequired = false) }
                    syncAndEvaluateDay(dailyGoalMinutes = 120)
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}