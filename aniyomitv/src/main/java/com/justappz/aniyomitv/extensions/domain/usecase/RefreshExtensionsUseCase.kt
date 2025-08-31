package com.justappz.aniyomitv.extensions.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions.domain.repo.ExtensionRepoDetailsRepository

class RefreshExtensionsUseCase(
    private val extensionRepoDetailsRepository: ExtensionRepoDetailsRepository,
) {
    suspend operator fun invoke(url: String): BaseUiState<Unit> =
        extensionRepoDetailsRepository.refreshExtensions(url)
}
