package com.justappz.aniyomitv.anime_search.domain.repo

import android.content.Context
import com.justappz.aniyomitv.anime_search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.base.BaseUiState

interface InstalledExtensionsRepo {
    fun getInstalledExtensions(context: Context): BaseUiState<List<InstalledExtensions>>
}
