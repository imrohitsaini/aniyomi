package com.justappz.aniyomitv.episodes.presentation.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import coil3.load
import coil3.request.crossfade
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.databinding.ActivityEpisodesBinding
import com.justappz.aniyomitv.extensions_management.utils.ExtensionUtils.loadAnimeSource
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

class EpisodesActivity : BaseActivity() {

    //region variables
    private lateinit var binding: ActivityEpisodesBinding
    private val tag = "EpisodesActivity"
    private var anime: SAnime? = null
    private var animeHttpSource: AnimeHttpSource? = null
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(tag, "onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_episodes)

        init()
    }
    //endregion

    //region init
    private fun init() {
        Log.d(tag, "init")

        anime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(IntentKeys.ANIME, SAnime::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(IntentKeys.ANIME) as? SAnime
        }

        val packageName = intent.getStringExtra(IntentKeys.ANIME_PKG)
        val className = intent.getStringExtra(IntentKeys.ANIME_CLASS)
        animeHttpSource = ctx.loadAnimeSource(packageName, className)

        binding.ivAnimeThumbnail.load(anime?.thumbnail_url) {
            crossfade(true)
        }
    }
    //endregion
}
