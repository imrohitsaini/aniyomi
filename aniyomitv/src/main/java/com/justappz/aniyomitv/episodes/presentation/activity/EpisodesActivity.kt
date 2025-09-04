package com.justappz.aniyomitv.episodes.presentation.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
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
import com.justappz.aniyomitv.constants.EpisodeWatchState
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
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import com.justappz.aniyomitv.playback.domain.model.toSEpisode
import com.justappz.aniyomitv.playback.presentation.activity.ExoPlayerActivity
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.SerializableVideo
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class EpisodesActivity : BaseActivity(), View.OnClickListener {

    //region variables
    private lateinit var binding: ActivityEpisodesBinding
    private val tag = "EpisodesActivity"
    private var anime: SAnime? = null
    private var selectedEpisode: SEpisode? = null
    private var selectedEpisodeDomain: EpisodeDomain? = null
    private lateinit var className: String
    private lateinit var packageName: String
    private var animeHttpSource: AnimeHttpSource? = null
    private lateinit var episodeAdapter: EpisodesAdapter
    private lateinit var loaderDialog: LoaderDialog
    private val viewModel: EpisodesViewModel by viewModels {
        ViewModelFactory {
            EpisodesViewModel(
                Injekt.get(),
                Injekt.get(),
                Injekt.get(),
                Injekt.get(),
                Injekt.get(),
            )
        }
    }
    private var nowPlayingPosition = -1
    val uploadingLibraryDialog = LoaderDialog("Updating library!")


    private enum class Sorting() {
        ASCENDING,
        DESCENDING
    }

    private var currentSorting: Sorting? = null
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

        val tintList = ContextCompat.getColorStateList(ctx, R.color.player_icon_selector)

        ImageViewCompat.setImageTintList(binding.ivLibrary, tintList)
        ImageViewCompat.setImageTintList(binding.ivResume, tintList)
        ImageViewCompat.setImageTintList(binding.ivSort, tintList)

        binding.ivLibrary.setOnClickListener(this)
        binding.ivResume.setOnClickListener(this)
        binding.ivSort.setOnClickListener(this)

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
                viewModel.getAnimeWithAnimeUrl(it.url)
            }
        }
        initLoader()
        observeAnimeLibraryUpdate()
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
                            currentSorting?.let {
                                episodeAdapter.updateList(sortEpisodes(state.data))
                            } ?: run {
                                episodeAdapter.updateList(state.data)
                            }
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
            animeHttpSource?.let { source ->
                nowPlayingPosition = position
                selectedEpisodeDomain = episode
                selectedEpisode = episode.toSEpisode()
                selectedEpisode?.let { viewModel.getVideosList(source, it) }
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
                putExtra(IntentKeys.ANIME_EPISODE, selectedEpisodeDomain)
            },
        )
    }
    //endregion

    //region onResume
    override fun onResume() {
        super.onResume()
        animeHttpSource?.let { source ->
            anime?.let {
                viewModel.getEpisodesList(source, it)
            }
        }
    }
    //endregion

    //region onClick
    override fun onClick(view: View?) {
        view?.let { v ->
            when (v) {
                binding.ivLibrary -> addRemoveLibrary()
                binding.ivSort -> {
                    episodeAdapter.updateList(sortEpisodes(episodeAdapter.getCurrentList()))
                }

                binding.ivResume -> selectNextEpisode()
            }
        }
    }
    //endregion

    //region sortEpisodes
    private fun sortEpisodes(currentList: List<EpisodeDomain>): List<EpisodeDomain> {
        binding.ivSort.isSelected = !binding.ivSort.isSelected

        if (currentSorting == null) currentSorting = Sorting.DESCENDING

        // Selected -> Ascending
        // Not selected -> Descending
        val sortedList = if (currentSorting == Sorting.DESCENDING) {
            currentSorting = Sorting.ASCENDING
            currentList.sortedBy { it.episodeNumber } // Ascending
        } else {
            currentSorting = Sorting.DESCENDING
            currentList.sortedByDescending { it.episodeNumber } // Descending
        }
        return sortedList
    }
    //endregion

    //region Manage Library
    private fun observeAnimeLibraryUpdate() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animeDomain.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
                            showUpdatingLibraryLoader(false)
                        }

                        is BaseUiState.Error -> {
                            showUpdatingLibraryLoader(false)
                        }

                        BaseUiState.Idle -> {
                        }

                        BaseUiState.Loading -> {
                            showUpdatingLibraryLoader(false)
                        }

                        is BaseUiState.Success<AnimeDomain?> -> {
                            showUpdatingLibraryLoader(false)
                            val animeDomain = state.data

                            animeDomain?.let {
                                // Anime exists in db, check it is in library
                                binding.ivLibrary.isSelected = it.inLibrary == true
                            } ?: run {
                                // Not in library as domain is null
                                binding.ivLibrary.isSelected = false
                            }
                            if (binding.ivLibrary.isSelected) {
                                binding.tvLibrary.setText(R.string.in_library)
                            } else {
                                binding.tvLibrary.setText(R.string.add_to_library)
                            }

                        }
                    }
                }
            }
        }
    }

    private fun addRemoveLibrary() {
        // Selected -> In the library
        // Not selected -> Not in the library
        anime?.let {
            if (binding.ivLibrary.isSelected) {
                // It is already in the library -> remove the anime and the episodes
                viewModel.updateAnimeWithDb(packageName, className, it, inLibrary = false)
            } else {
                // Not in the library -> add the anime and episodes in the library
                viewModel.updateAnimeWithDb(packageName, className, it, inLibrary = true)
            }
        }
    }

    private fun showUpdatingLibraryLoader(toShow: Boolean) {
        if (toShow && !uploadingLibraryDialog.isRunning) {
            uploadingLibraryDialog.show(supportFragmentManager, "uploading_library")
        } else if (!toShow && uploadingLibraryDialog.isRunning) {
            uploadingLibraryDialog.dismiss()
        }
    }
    //endregion

    //region Episode Scroll
    fun selectNextEpisode() {
        val list = episodeAdapter.getCurrentList()
        var index = if (currentSorting == Sorting.ASCENDING) {
            list.indexOfLast {
                it.watchState == EpisodeWatchState.IN_PROGRESS || it.watchState == EpisodeWatchState.WATCHED
            }.takeIf { it != -1 } ?: 0
        } else {
            list.indexOfFirst {
                it.watchState == EpisodeWatchState.IN_PROGRESS || it.watchState == EpisodeWatchState.WATCHED
            }.takeIf { it != -1 } ?: list.lastIndex
        }

        // Adjust if the found one is WATCHED (not IN_PROGRESS)
        if (index in list.indices && list[index].watchState == EpisodeWatchState.WATCHED) {
            index = if (currentSorting == Sorting.ASCENDING) {
                (index + 1).coerceAtMost(list.lastIndex) // cap at last index
            } else {
                (index - 1).coerceAtLeast(0) // floor at 0
            }
        }
        Log.d(tag, "scroll to $index")
        showLoading(true, binding.episodesLoading)
        binding.rvEpisodes.isVisible = false

        binding.rvEpisodes.post {
            binding.rvEpisodes.scrollToPosition(index)
        }

        showLoading(false, binding.episodesLoading)
        binding.rvEpisodes.isVisible = true


        lifecycleScope.launch(Dispatchers.Main) {
            delay(500)
            binding.rvEpisodes.post {
                binding.rvEpisodes.scrollToPosition(index)
                val vh = binding.rvEpisodes.findViewHolderForAdapterPosition(index)
                vh?.itemView?.requestFocus()
            }
        }
    }
    //endregion
}

