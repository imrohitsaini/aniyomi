package com.justappz.aniyomitv.extensions_management.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.util.UrlUtils
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepoDetailsRepository

class InsertExtensionRepoUrlUseCase(
    private val extensionRepoDetailsRepository: ExtensionRepoDetailsRepository,
) {
    suspend operator fun invoke(url: String): BaseUiState<Unit> {
        val domain = ExtensionRepositoriesDetailsDomain(
            repoUrl = url,
            cleanName = UrlUtils.getCleanUrl(url),
            dateAdded = System.currentTimeMillis(),
        )
        return extensionRepoDetailsRepository.addRepo(domain)
    }
}
