package com.justappz.aniyomitv.anime_search.domain.model

import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

data class InstalledExtensions(
    val packageName: String,
    val className: String,
    val nsfwFlag: Int,
    val instance: AnimeHttpSource?,
    val appName: String,
    var isSelected: Boolean? = false
)
