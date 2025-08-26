package com.justappz.aniyomitv.anime_search.domain.usecase

import android.content.Context
import com.justappz.aniyomitv.anime_search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.anime_search.domain.repo.InstalledExtensionsRepo

class GetInstalledExtensionsUseCase(
    private val installedExtensionsRepo: InstalledExtensionsRepo,
) {
    operator fun invoke(context: Context): List<InstalledExtensions> =
        installedExtensionsRepo.getInstalledExtensions(context)
}
