package com.justappz.aniyomitv.playback.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
object ExoCache {
    @Volatile
    private var cache: SimpleCache? = null

    @SuppressLint("UnsafeOptInUsageError")
    fun get(ctx: Context): SimpleCache {
        return cache ?: synchronized(this) {
            cache ?: buildCache(ctx).also { cache = it }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun buildCache(ctx: Context): SimpleCache {
        val cacheDir = File(ctx.cacheDir, "media_cache")
        val databaseProvider = StandaloneDatabaseProvider(ctx)
        val evictor = LeastRecentlyUsedCacheEvictor(100L * 1024L * 1024L) // 100MB
        return SimpleCache(cacheDir, evictor, databaseProvider)
    }
}
