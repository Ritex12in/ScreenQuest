package com.screenquest.domain.usecase

import com.screenquest.domain.repository.IGameRepository
import javax.inject.Inject

class CompleteQuestUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(questId: Long) {
        gameRepository.completeQuest(questId)
    }
}