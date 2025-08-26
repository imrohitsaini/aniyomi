package com.justappz.aniyomitv.anime_search.domain.repo

import android.content.Context
import com.justappz.aniyomitv.anime_search.domain.model.InstalledExtensions

interface InstalledExtensionsRepo {
    fun getInstalledExtensions(context: Context): List<InstalledExtensions>
}
