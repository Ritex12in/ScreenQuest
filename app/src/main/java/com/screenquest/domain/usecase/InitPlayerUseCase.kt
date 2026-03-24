package com.screenquest.domain.usecase

import com.screenquest.domain.repository.IGameRepository
import com.screenquest.domain.repository.IUsageRepository
import javax.inject.Inject

class InitPlayerUseCase @Inject constructor(
    private val gameRepository: IGameRepository,
    private val usageRepository: IUsageRepository
) {
    suspend operator fun invoke(): InitResult {
        if (!usageRepository.hasUsagePermission()) {
            return InitResult.PermissionRequired
        }
        gameRepository.initProfileIfMissing()
        return InitResult.Ready
    }
}

sealed class InitResult {
    object Ready : InitResult()
    object PermissionRequired : InitResult()
}