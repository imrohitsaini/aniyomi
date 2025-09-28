package com.justappz.aniyomitv.discover.domain.repo

import android.content.Context
import com.justappz.aniyomitv.discover.domain.model.InstalledExtensions
import com.justappz.aniyomitv.base.BaseUiState

interface InstalledExtensionsRepo {
    fun getInstalledExtensions(context: Context): BaseUiState<List<InstalledExtensions>>
}
