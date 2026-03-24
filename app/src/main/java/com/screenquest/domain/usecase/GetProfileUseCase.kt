package com.screenquest.domain.usecase

import com.screenquest.domain.model.Player
import com.screenquest.domain.model.XPTable
import com.screenquest.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class ProfileData(
    val player: Player,
    val levelTitle: String,
    val nextLevelXP: Int,
    val progressFraction: Float
)

class GetProfileUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    operator fun invoke(): Flow<ProfileData?> {
        return gameRepository.observePlayer().map { player ->
            player?.let {
                ProfileData(
                    player = it,
                    levelTitle = XPTable.levelTitle(it.currentLevel),
                    nextLevelXP = it.xpForNextLevel,
                    progressFraction = it.levelProgressFraction
                )
            }
        }
    }
}