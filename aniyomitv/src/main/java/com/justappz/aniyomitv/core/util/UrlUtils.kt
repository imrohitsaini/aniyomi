package com.justappz.aniyomitv.core.util

import android.util.Patterns


object UrlUtils {

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

    fun getCleanUrl(url: String): String {
        return try {
            val uri = java.net.URI(url)

            return when {
                // Case 1: GitHub raw URL
                uri.host.contains("githubusercontent.com") -> {
                    val segments = uri.path.split("/").filter { it.isNotBlank() }
                    // raw.githubusercontent.com/<owner>/<repo>/...
                    segments.getOrNull(0) ?: url
                }

                // Case 2: Custom domain
                else -> {
                    val segments = uri.path.split("/").filter { it.isNotBlank() }
                    // e.g. kohiden.xyz/Kohi-den/extensions/...
                    segments.getOrNull(0) ?: uri.host
                }
            }
        } catch (e: Exception) {
            url // fallback to original
        }
    }
}
