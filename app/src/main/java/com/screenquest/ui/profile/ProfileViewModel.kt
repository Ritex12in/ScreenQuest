package com.screenquest.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screenquest.domain.model.Player
import com.screenquest.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProfileUiState(
    val player: Player? = null,
    val levelTitle: String = "",
    val nextLevelXP: Int = 0,
    val progressFraction: Float = 0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getProfile: GetProfileUseCase
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = getProfile()
        .map { data ->
            if (data == null) return@map ProfileUiState(isLoading = false)
            ProfileUiState(
                player = data.player,
                levelTitle = data.levelTitle,
                nextLevelXP = data.nextLevelXP,
                progressFraction = data.progressFraction,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState()
        )
}