package com.justappz.aniyomitv.extensions_management.domain.usecase

import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepo

class GetExtensionUseCase(
    private val repository: ExtensionRepo,
) {
    operator fun invoke(url: String): List<ExtensionDomain> {
        return repository.getExtensions(url)
    }
}
