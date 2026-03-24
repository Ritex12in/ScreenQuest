package com.screenquest.domain.usecase

import com.screenquest.domain.model.Quest
import com.screenquest.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class QuestsData(
    val active: List<Quest>,
    val completed: List<Quest>
)

class GetQuestsUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    operator fun invoke(): Flow<QuestsData> {
        return combine(
            gameRepository.observeActiveQuests(),
            gameRepository.observeCompletedQuests()
        ) { active, completed ->
            QuestsData(active = active, completed = completed)
        }
    }
}