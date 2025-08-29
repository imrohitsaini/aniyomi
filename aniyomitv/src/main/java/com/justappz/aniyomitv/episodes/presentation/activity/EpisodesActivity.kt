package com.justappz.aniyomitv.episodes.presentation.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil3.load
import coil3.request.crossfade
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.databinding.ActivityEpisodesBinding
import com.justappz.aniyomitv.episodes.presentation.viewmodel.EpisodesViewModel
import com.justappz.aniyomitv.extensions_management.utils.ExtensionUtils.loadAnimeSource
import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class EpisodesActivity : BaseActivity() {

    //region variables
    private lateinit var binding: ActivityEpisodesBinding
    private val tag = "EpisodesActivity"
    private var anime: SAnime? = null
    private var animeHttpSource: AnimeHttpSource? = null
    private val viewModel: EpisodesViewModel by viewModels {
        ViewModelFactory { EpisodesViewModel(Injekt.get()) }
    }
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

        observeAnimeDetails()
        anime?.let { viewModel.getAnimeDetails(animeHttpSource as AnimeCatalogueSource, it) }
    }
    //endregion

    //region observeAnimeDetails
    private fun observeAnimeDetails() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animeDetailsState.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
                            showLoading(false)
                            Log.d(tag, "anime details empty")
                        }

                        is BaseUiState.Error -> {
                            showLoading(false)

                            Log.d(tag, "anime details error")
                        }

                        BaseUiState.Idle -> {
                            showLoading(false)
                            Log.d(tag, "anime details idle")
                        }

                        BaseUiState.Loading -> {
                            showLoading(true)
                            Log.d(tag, "anime details loading")
                        }

                        is BaseUiState.Success -> {
                            Log.d(tag, "anime details success")
                            anime?.let {
                                it.description = state.data.description
                                it.author = state.data.author
                                it.genre = state.data.genre
                                it.initialized = state.data.initialized
                                it.status = state.data.status
                                it.update_strategy = state.data.update_strategy
                            }
                            anime?.let { updateAnimeDetails(it) }
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region updateAnimeDetails
    private fun updateAnimeDetails(anime: SAnime) {
        binding.tvAnimeTitle.text = anime.title
        binding.tvDescription.text = anime.description
        binding.tvGenre.text = getString(R.string.genre, anime.genre)
    }
    //endregion

    //region Show Loading
    private fun showLoading(toShow: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.detailsLoading.isVisible = toShow
            Log.d(tag, "loader $toShow")
        }
    }
    //endregion
}
