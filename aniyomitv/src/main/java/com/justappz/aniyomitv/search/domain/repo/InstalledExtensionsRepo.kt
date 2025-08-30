package com.justappz.aniyomitv.search.domain.repo

import android.content.Context
import com.justappz.aniyomitv.search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.base.BaseUiState

interface InstalledExtensionsRepo {
    fun getInstalledExtensions(context: Context): BaseUiState<List<InstalledExtensions>>
}
