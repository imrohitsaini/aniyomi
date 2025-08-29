package com.justappz.aniyomitv.extensions_management.domain.usecase

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.extensions_management.domain.model.RepoDomain
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepo

class GetExtensionUseCase(
    private val repository: ExtensionRepo,
) {
    operator fun invoke(url: String): BaseUiState<RepoDomain> {
        return repository.getExtensions(url)
    }
}
