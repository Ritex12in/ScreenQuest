package com.screenquest.ui.quests


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screenquest.domain.model.Quest
import com.screenquest.domain.usecase.CompleteQuestUseCase
import com.screenquest.domain.usecase.GetQuestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestsUiState(
    val activeQuests: List<Quest> = emptyList(),
    val completedQuests: List<Quest> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class QuestsViewModel @Inject constructor(
    getQuests: GetQuestsUseCase,
    private val completeQuest: CompleteQuestUseCase
) : ViewModel() {

    val uiState: StateFlow<QuestsUiState> = getQuests()
        .map { data ->
            QuestsUiState(
                activeQuests = data.active,
                completedQuests = data.completed,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuestsUiState()
        )

    fun onQuestCompleted(questId: Long) {
        viewModelScope.launch {
            completeQuest(questId)
        }
    }
}