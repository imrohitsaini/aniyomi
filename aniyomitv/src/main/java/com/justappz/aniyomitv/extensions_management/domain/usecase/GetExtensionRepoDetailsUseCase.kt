package com.justappz.aniyomitv.extensions_management.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepoDetailsRepository

class GetExtensionRepoDetailsUseCase(
    private val extensionRepoDetailsRepository: ExtensionRepoDetailsRepository,
) {
    suspend operator fun invoke(): BaseUiState<List<ExtensionRepositoriesDetailsDomain>> =
        extensionRepoDetailsRepository.getRepos()
}
