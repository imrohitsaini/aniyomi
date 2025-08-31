package com.justappz.aniyomitv.extensions.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions.domain.model.RepoDomain
import com.justappz.aniyomitv.extensions.domain.repo.ExtensionRepoDetailsRepository
import kotlinx.coroutines.flow.Flow

class ObserveExtensionsUseCase(
    private val extensionRepoDetailsRepository: ExtensionRepoDetailsRepository,
) {
    suspend operator fun invoke(url: String): Flow<BaseUiState<RepoDomain>> =
        extensionRepoDetailsRepository.loadExtensionsFromDB(url)
}
