package com.justappz.aniyomitv.core.util

import android.util.Patterns


object ValidationUtils {

    /**
     * Checks if the given string is a valid URL.
     */
    fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return Patterns.WEB_URL.matcher(url.trim()).matches()
    }
}
