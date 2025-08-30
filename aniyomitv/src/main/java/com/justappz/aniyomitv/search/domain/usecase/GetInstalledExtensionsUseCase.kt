package com.justappz.aniyomitv.search.domain.usecase

import android.content.Context
import com.justappz.aniyomitv.search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.search.domain.repo.InstalledExtensionsRepo
import com.justappz.aniyomitv.base.BaseUiState

class GetInstalledExtensionsUseCase(
    private val installedExtensionsRepo: InstalledExtensionsRepo,
) {
    operator fun invoke(context: Context): BaseUiState<List<InstalledExtensions>> =
        installedExtensionsRepo.getInstalledExtensions(context)
}
