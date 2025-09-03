package com.justappz.aniyomitv.core.util

object StringUtils {
    fun String.getAnimeKeyFromUrl(sep: String = "<&sep>"): String {
        val index = this.indexOf(sep)
        return if (index != -1) this.substring(0, index) else this
    }
}
