package com.justappz.aniyomitv.episodes.presentation.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import coil3.load
import coil3.request.crossfade
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.dialog.LoaderDialog
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.core.error.ErrorHandler
import com.justappz.aniyomitv.databinding.ActivityEpisodesBinding
import com.justappz.aniyomitv.episodes.presentation.adapters.EpisodesAdapter
import com.justappz.aniyomitv.episodes.presentation.viewmodel.EpisodesViewModel
import com.justappz.aniyomitv.extensions.utils.ExtensionUtils.loadAnimeSource
import com.justappz.aniyomitv.playback.presentation.activity.ExoPlayerActivity
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.SerializableVideo
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
    private var selectedEpisode: SEpisode? = null
    private lateinit var className: String
    private lateinit var packageName: String
    private var animeHttpSource: AnimeHttpSource? = null
    private lateinit var episodeAdapter: EpisodesAdapter
    private lateinit var loaderDialog: LoaderDialog
    private val viewModel: EpisodesViewModel by viewModels {
        ViewModelFactory { EpisodesViewModel(Injekt.get(), Injekt.get(), Injekt.get()) }
    }
    private var nowPlayingPosition = -1
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
            @Suppress("DEPRECATION") intent.getSerializableExtra(IntentKeys.ANIME) as? SAnime
        }

        packageName = intent.getStringExtra(IntentKeys.ANIME_PKG).toString()
        className = intent.getStringExtra(IntentKeys.ANIME_CLASS).toString()
        animeHttpSource = ctx.loadAnimeSource(packageName, className)

        binding.ivAnimeThumbnail.load(anime?.thumbnail_url) {
            crossfade(true)
        }

        observeAnimeDetails()
        observeEpisodes()
        observeVideos()
        setEpisodeProperties()
        animeHttpSource?.let { source ->
            anime?.let {
                viewModel.getAnimeDetails(source, it)
                viewModel.getEpisodesList(source, it)
            }
        }
        initLoader()
    }
    //endregion

    //region observeAnimeDetails
    private fun observeAnimeDetails() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animeDetailsState.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
                            showLoading(false, binding.detailsLoading)
                            Log.d(tag, "anime details empty")
                        }

                        is BaseUiState.Error -> {
                            showLoading(false, binding.detailsLoading)

                            Log.d(tag, "anime details error")
                        }

                        BaseUiState.Idle -> {
                            showLoading(false, binding.detailsLoading)
                            Log.d(tag, "anime details idle")
                        }

                        BaseUiState.Loading -> {
                            showLoading(true, binding.detailsLoading)
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
                            showLoading(false, binding.detailsLoading)
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region observeEpisodes
    private fun observeEpisodes() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.episodesList.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
                            showLoading(false, binding.episodesLoading)
                            Log.d(tag, "episodesList empty")
                        }

                        is BaseUiState.Error -> {
                            showLoading(false, binding.episodesLoading)
                            Log.d(tag, "episodesList error")
                        }

                        BaseUiState.Idle -> {
                            showLoading(false, binding.episodesLoading)
                            Log.d(tag, "episodesList idle")
                        }

                        BaseUiState.Loading -> {
                            showLoading(true, binding.episodesLoading)
                            Log.d(tag, "episodesList loading")
                        }

                        is BaseUiState.Success -> {
                            Log.d(tag, "episodesList success")
                            episodeAdapter.updateList(state.data)
                            showLoading(false, binding.episodesLoading)
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region observeVideos
    private fun observeVideos() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videosList.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
                            showDialogLoader(false)
                            Log.d(tag, "videosList empty")
                            ErrorHandler.show(
                                ctx,
                                AppError.UnknownError(
                                    message = "No videos found",
                                    displayType = ErrorDisplayType.TOAST,
                                ),
                            )
                        }

                        is BaseUiState.Error -> {
                            showDialogLoader(false)
                            Log.d(tag, "videosList error")
                            ErrorHandler.show(ctx, state.error)
                        }

                        BaseUiState.Idle -> {
                            showDialogLoader(false)
                            Log.d(tag, "videosList idle")
                        }

                        BaseUiState.Loading -> {
                            showDialogLoader(true)
                            Log.d(tag, "videosList loading")
                        }

                        is BaseUiState.Success -> {
                            Log.d(tag, "videosList success ${state.data.size}")
                            showDialogLoader(false)
                            val serialized = with(SerializableVideo.Companion) {
                                state.data.serialize()
                            }
                            openPlayer(serialized)
                            viewModel.resetVideoState()
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region initLoader
    private fun initLoader() {
        loaderDialog = LoaderDialog(title = "Searching for videos...!")
    }

    private fun showDialogLoader(toShow: Boolean) {
        if (toShow && !loaderDialog.isRunning) {
            loaderDialog.show(supportFragmentManager, "loader")
        } else if (loaderDialog.isRunning) {
            loaderDialog.dismiss()
        }
    }
    //endregion

    //region setEpisodeProperties
    private fun setEpisodeProperties() {
        episodeAdapter = EpisodesAdapter(emptyList())
        episodeAdapter.onItemClick = { episode, position ->
            animeHttpSource?.let {
                nowPlayingPosition = position
                selectedEpisode = episode
                viewModel.getVideosList(it, episode)
            }
        }
        binding.rvEpisodes.layoutManager = GridLayoutManager(ctx, 4)
        binding.rvEpisodes.adapter = episodeAdapter
    }
    //endregion

    //region updateAnimeDetails
    private fun updateAnimeDetails(anime: SAnime) {
        binding.tvAnimeTitle.text = anime.title
        binding.tvDescription.text = anime.description
        binding.tvGenre.text = getString(R.string.genre, anime.genre)
    }
    //endregion

    //region showLoading
    private fun showLoading(toShow: Boolean, progressBar: View) {
        lifecycleScope.launch(Dispatchers.Main) {
            progressBar.isVisible = toShow
            Log.d(tag, "loader $toShow")
        }
    }
    //endregion

    //region openPlayer
    private fun openPlayer(serialized: String) {
        startActivity(
            Intent(ctx, ExoPlayerActivity::class.java).apply {
                putExtra(IntentKeys.SOURCE_LIST, serialized)
                putExtra(IntentKeys.NOW_PLAYING, nowPlayingPosition)
                putExtra(IntentKeys.ANIME, anime)
                putExtra(IntentKeys.ANIME_CLASS, className)
                putExtra(IntentKeys.ANIME_PKG, packageName)
                putExtra(IntentKeys.ANIME_EPISODE, selectedEpisode)
            },
        )
    }
    //endregion
}
