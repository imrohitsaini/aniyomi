package com.justappz.aniyomitv.core.util

import android.util.Patterns


object ValidationUtils {

    /**
     * Checks if the given string is a valid repo URL.
     * A valid repo URL must be a proper web URL AND end with "index.min.json".
     */
    fun isValidRepoUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val trimmedUrl = url.trim()
        return Patterns.WEB_URL.matcher(trimmedUrl).matches() &&
            trimmedUrl.endsWith("index.min.json", ignoreCase = true)
    }
}
