package com.justappz.aniyomitv.playback.presentation.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.constants.EpisodeWatchState
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.dialog.ExitDialogFragment
import com.justappz.aniyomitv.core.components.dialog.RadioButtonDialog
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.core.error.ErrorHandler
import com.justappz.aniyomitv.core.model.Options
import com.justappz.aniyomitv.core.model.RadioButtonDialogModel
import com.justappz.aniyomitv.core.util.FocusKeyHandler
import com.justappz.aniyomitv.core.util.UserDefinedErrors
import com.justappz.aniyomitv.core.util.UserDefinedErrors.SOMETHING_WENT_WRONG
import com.justappz.aniyomitv.databinding.ActivityExoPlayerBinding
import com.justappz.aniyomitv.extensions.utils.ExtensionUtils.loadAnimeSource
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain
import com.justappz.aniyomitv.playback.domain.model.toSEpisode
import com.justappz.aniyomitv.playback.presentation.viewmodel.PlayerViewModel
import com.justappz.aniyomitv.playback.utils.ExoCache
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.SerializableVideo.Companion.toVideoList
import eu.kanade.tachiyomi.animesource.model.Video
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Headers
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.Locale

class ExoPlayerActivity : BaseActivity(), View.OnClickListener {

    //region variables
    private lateinit var binding: ActivityExoPlayerBinding
    private val tag = "ExoPlayerActivity"

    private lateinit var sourceList: List<Video>
    private var preferredSource = 0

    private lateinit var anime: SAnime

    private lateinit var className: String
    private lateinit var packageName: String
    private lateinit var animeHttpSource: AnimeHttpSource

    private lateinit var selectedEpisode: EpisodeDomain

    private lateinit var nextEpisode: EpisodeDomain
    private var nextSourcesList: List<Video> = arrayListOf()


    private var allEpisodeList: List<EpisodeDomain> = arrayListOf()
    private var lastEpisodeUpdateTime = 0L

    private var doubleBackToExitPressedOnce = false

    private var exoPlayer: ExoPlayer? = null
    private val backHandler = Handler(Looper.getMainLooper())
    private val controlsHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { setControlsVisible(false) }
    private val progressHandler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    private var seekJob: Job? = null
    private var resumePosition = 0L

    private var canUpdateEpisode = true

    private var exitDialog: ExitDialogFragment? = null

    private val viewModel: PlayerViewModel by viewModels {
        ViewModelFactory {
            PlayerViewModel(
                Injekt.get(),
                Injekt.get(),
                Injekt.get(),
            )
        }
    }

    //endregion

    //region onCreate
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exo_player)

        val sanime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(IntentKeys.ANIME, SAnime::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getSerializableExtra(IntentKeys.ANIME) as? SAnime
        }

        if (sanime == null) {
            ErrorHandler.show(ctx, SOMETHING_WENT_WRONG)
            finish()
        } else {
            anime = sanime
        }

        observeEpisodeUpdate()
        getAllEpisodeObserver()
        nextVideoListObserver()
        viewModel.getAllEpisodes(anime.url)


        val sepisode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(IntentKeys.ANIME_EPISODE, EpisodeDomain::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getSerializableExtra(IntentKeys.ANIME_EPISODE) as? EpisodeDomain
        }
        if (sepisode == null) {
            ErrorHandler.show(ctx, SOMETHING_WENT_WRONG)
            finish()
        } else {
            selectedEpisode = sepisode
        }

        packageName = intent.getStringExtra(IntentKeys.ANIME_PKG).toString()
        className = intent.getStringExtra(IntentKeys.ANIME_CLASS).toString()
        val source = ctx.loadAnimeSource(packageName, className)

        if (source == null) {
            ErrorHandler.show(ctx, UserDefinedErrors.UNABLE_TO_LOAD_EXTENSION)
            finish()
        } else {
            animeHttpSource = source
        }

        // handle intent data
        sourceList = intent.getStringExtra(IntentKeys.SOURCE_LIST)?.toVideoList() ?: emptyList()
        Log.d(tag, "videos ${sourceList.size}")

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (doubleBackToExitPressedOnce) {
                        showExitDialog()
                    } else {
                        binding.bottomBar.seekBar.requestFocus()
                        toggleControls()
                        doubleBackToExitPressedOnce = true

                        backHandler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000) // 2s window
                    }
                }
            },
        )

        resumePosition = if (selectedEpisode.watchState == EpisodeWatchState.WATCHED) {
            0L
        } else {
            selectedEpisode.lastWatchTime
        }
        init()
        startPlayer(selectedEpisode, preferredSource, resumePosition)

    }
    //endregion

    //region init
    private fun init() {
        binding.topBar.tvAnimeTitle.text = anime.title
        binding.topBar.ivBack.setOnClickListener(this)
        binding.topBar.ivSource.setOnClickListener(this)

        binding.ivPlayPause.setOnClickListener(this)
        binding.playerView.setOnClickListener(this)

        binding.bottomBar.ivPlayNext.setOnClickListener(this)
        binding.bottomBar.ivPlayPauseBottombar.setOnClickListener(this)
        binding.bottomBar.ivBackward.setOnClickListener(this)
        binding.bottomBar.ivForward.setOnClickListener(this)

        binding.bottomBar.ivForward.setOnFocusChangeListener { _, hasFocus ->
            binding.bottomBar.tvForward.setTextColor(
                ContextCompat.getColor(this, if (hasFocus) R.color.anime_tv_primary else R.color.white),
            )
        }

        binding.bottomBar.ivBackward.setOnFocusChangeListener { _, hasFocus ->
            binding.bottomBar.tvBackward.setTextColor(
                ContextCompat.getColor(this, if (hasFocus) R.color.anime_tv_primary else R.color.white),
            )
        }

        val tintList = ContextCompat.getColorStateList(ctx, R.color.player_icon_selector)

        ImageViewCompat.setImageTintList(binding.topBar.ivBack, tintList)
        ImageViewCompat.setImageTintList(binding.topBar.ivSource, tintList)

        ImageViewCompat.setImageTintList(binding.ivPlayPause, tintList)

        ImageViewCompat.setImageTintList(binding.bottomBar.ivPlayPauseBottombar, tintList)
        ImageViewCompat.setImageTintList(binding.bottomBar.ivPlayNext, tintList)
        ImageViewCompat.setImageTintList(binding.bottomBar.ivBackward, tintList)
        ImageViewCompat.setImageTintList(binding.bottomBar.ivForward, tintList)

        binding.playerView.setOnKeyListener(
            FocusKeyHandler(
                onCenter = {
                    togglePlayPause()
                    return@FocusKeyHandler true
                },
                onDown = {
                    setControlsVisible(true)
                    binding.bottomBar.seekBar.requestFocus()
                    return@FocusKeyHandler true
                },
                onUp = {
                    setControlsVisible(true)
                    binding.topBar.ivBack.requestFocus()
                    return@FocusKeyHandler true
                },
            ),
        )


        binding.topBar.ivBack.setOnKeyListener(
            FocusKeyHandler(
                onDown = {
                    if (binding.ivPlayPause.isVisible) {
                        binding.playerView.requestFocus()
                    } else {
                        binding.bottomBar.seekBar.requestFocus()
                    }
                    return@FocusKeyHandler true
                },
            ),
        )

        binding.bottomBar.seekBar.setOnKeyListener(
            FocusKeyHandler(
                onUp = {
                    if (binding.ivPlayPause.isVisible) {
                        binding.playerView.requestFocus()
                    } else {
                        binding.topBar.ivBack.requestFocus()
                    }
                    return@FocusKeyHandler true
                },
                onDown = {
                    binding.bottomBar.ivForward.requestFocus()
                    return@FocusKeyHandler true
                },
            ),
        )
    }
    //endregion

    //region previous or next episodes
    private fun isLastEpisode(): Boolean {
        val index = allEpisodeList.indexOfFirst { it.episodeNumber > selectedEpisode.episodeNumber }
        return index == -1
    }

    private fun readyNextEpisode() {
        try {
            val index = allEpisodeList.indexOfFirst { it.episodeNumber > selectedEpisode.episodeNumber }
            nextEpisode = allEpisodeList[index]

            viewModel.getVideosList(animeHttpSource, nextEpisode.toSEpisode())
        } catch (e: Exception) {
            Log.d(tag, "Next episode error ${e.message}")
            ErrorHandler.show(ctx, SOMETHING_WENT_WRONG)
        }
    }

    //region observeVideos
    private fun nextVideoListObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videosList.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
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
                            Log.d(tag, "videosList error")
                            ErrorHandler.show(ctx, state.error)
                        }

                        BaseUiState.Idle -> {
                            Log.d(tag, "videosList idle")
                        }

                        BaseUiState.Loading -> {
                            Log.d(tag, "videosList loading")
                        }

                        is BaseUiState.Success -> {
                            Log.d(tag, "next video list success ${state.data.size}")
                            nextSourcesList = state.data
                            viewModel.resetVideoState()
                        }
                    }
                }
            }
        }
    }
    //endregion
    //endregion

    //region seekbar
    @SuppressLint("SetTextI18n")
    private fun initSeekBar() {
        val seekBar = binding.bottomBar.seekBar
        val tvCurrent = binding.bottomBar.tvCurrentTime
        val tvTotal = binding.bottomBar.tvTotalTime

        // Player listener for duration
        exoPlayer?.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        binding.loading.isVisible = false
                        tvTotal.text = formatTime(exoPlayer?.duration ?: 0L)
                        seekBar.max = (exoPlayer?.duration ?: 0L).toInt()
                    } else if (state == Player.STATE_BUFFERING) {
                        binding.loading.isVisible = true
                    }
                }
            },
        )

        // Normal + TV scrubbing
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        // User is scrubbing (TV or touch)
                        tvCurrent.text = formatTime(progress.toLong())
                        isUserSeeking = true

                        // restart idle commit timer
                        startSeekIdleJob(progress.toLong())
                    }
                    val ratio = progress / sb!!.max.toFloat()
                    val availableWidth = sb.width - sb.paddingLeft - sb.paddingRight
                    val posX = sb.paddingLeft + ratio * availableWidth
                    binding.bottomBar.seekThumb.translationX = posX - binding.bottomBar.seekThumb.width / 2f

                }

                override fun onStartTrackingTouch(sb: SeekBar?) {
                    isUserSeeking = true
                    cancelSeekJob()
                }

                override fun onStopTrackingTouch(sb: SeekBar?) {
                    // touch-based commit (phones/tablets)
                    sb?.let { seekToPosition(it.progress.toLong()) }
                    isUserSeeking = false
                    startControlsAutoHideTimer()
                }
            },
        )

        // TV focus listener
        seekBar.setOnFocusChangeListener { v, hasFocus ->
            isUserSeeking = hasFocus
            if (!hasFocus) {
                // Leaving focus → just let idle job finish
                cancelSeekJob()
            }
            val startHeight = v.height
            val endHeight = if (hasFocus) {
                resources.getDimensionPixelSize(R.dimen._4dp) // bigger
            } else {
                resources.getDimensionPixelSize(R.dimen._2dp) // smaller
            }

            ValueAnimator.ofInt(startHeight, endHeight).apply {
                duration = 200 // ms
                interpolator = DecelerateInterpolator()
                addUpdateListener { animator ->
                    v.layoutParams.height = animator.animatedValue as Int
                    v.requestLayout()
                }
                start()
            }

            // Animate overlay thumb size
            val scale = if (hasFocus) 1.25f else 1f

            binding.bottomBar.seekThumb.animate().scaleX(scale).scaleY(scale).setDuration(200)
                .setInterpolator(DecelerateInterpolator()).start()
        }

        // Progress updater
        val updateProgress = object : Runnable {
            override fun run() {
                if (!isUserSeeking && exoPlayer?.isPlaying == true) {
                    val position = exoPlayer?.currentPosition ?: 0L
                    val duration = exoPlayer?.duration ?: 1L // avoid divide
                    seekBar.progress = position.toInt()
                    tvCurrent.text = formatTime(position)

                    val now = System.currentTimeMillis()

                    if (now - lastEpisodeUpdateTime >= 10000) {
                        val progressPercent = (position.toDouble() / duration.toDouble()) * 100
                        val watchState =
                            if (progressPercent >= 80) EpisodeWatchState.WATCHED else EpisodeWatchState.IN_PROGRESS
                        if (canUpdateEpisode) {
                            updateEpisodeWithDb(position, selectedEpisode.toSEpisode(), watchState)
                            canUpdateEpisode = false
                        }
                    }
                }
                progressHandler.postDelayed(this, 500)
            }
        }
        progressHandler.post(updateProgress)
    }

    private fun updateDurationUi() {
        val tvTotal = binding.bottomBar.tvTotalTime
        val seekBar = binding.bottomBar.seekBar
        val duration = exoPlayer?.duration ?: 0L

        tvTotal.text = formatTime(duration)
        seekBar.max = duration.toInt()
    }

    private fun startSeekIdleJob(position: Long) {
        cancelSeekJob()
        seekJob = lifecycleScope.launch {
            delay(500) // wait 1s of inactivity
            seekToPosition(position)
            isUserSeeking = false
            setControlsVisible(true)
            startControlsAutoHideTimer()
        }
    }

    private fun cancelSeekJob() {
        seekJob?.cancel()
        seekJob = null
    }

    private fun seekToPosition(position: Long) {
        exoPlayer?.seekTo(position)
        if (exoPlayer?.isPlaying == false) togglePlayPause()
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
    }
    //endregion

    //region startPlayer
    fun Headers.toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (i in 0 until size) {
            map[name(i)] = value(i)
        }
        return map
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startPlayer(selectedEpisode: EpisodeDomain, selectedSourcePosition: Int, resumePosition: Long) {
        val video = sourceList[selectedSourcePosition]

        val mediaItem = MediaItem.fromUri(video.videoUrl)

        if (exoPlayer == null) {
            val dataSourceFactory =
                DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true).setConnectTimeoutMs(15_000)
                    .setReadTimeoutMs(30_000).setDefaultRequestProperties(video.headers?.toMap() ?: emptyMap())

            val cache = ExoCache.get(ctx)

            val cacheDataSourceFactory =
                CacheDataSource.Factory().setCache(cache).setUpstreamDataSourceFactory(dataSourceFactory)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

            val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(
                10_000, // minBufferMs: 10s
                50_000, // maxBufferMs: 50s
                1_000,  // bufferForPlaybackMs: start playback after 1s buffered
                2_000,   // bufferForPlaybackAfterRebufferMs
            ).build()

            // First-time init
            exoPlayer = ExoPlayer.Builder(this).setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
                .setLoadControl(loadControl).build().apply {
                    binding.playerView.player = this

                    addListener(
                        object : Player.Listener {
                            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                                if (playbackState == Player.STATE_READY) {
                                    updateDurationUi()
                                    setControlsVisible(true)
                                    startControlsAutoHideTimer()
                                    binding.errorRoot.tvError.text = ""
                                    binding.errorRoot.root.isVisible = false
                                }
                            }

                            override fun onPlayerError(error: PlaybackException) {
                                Log.e(tag, "ExoPlayer error: ${error.message}", error)
                                Toast.makeText(
                                    this@ExoPlayerActivity,
                                    "Error: ${error.errorCodeName}",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                binding.errorRoot.tvError.text = getString(R.string.please_select_different_source)
                                binding.errorRoot.root.isVisible = true
                            }
                        },
                    )
                }
            initSeekBar()
        }

        // UI operations must be on the main thread
        runOnUiThread {
            // Bind to PlayerView
            binding.playerView.player = exoPlayer

            // Set media and start playback
            exoPlayer?.stop()
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()

            if (resumePosition > 0) {
                exoPlayer?.seekTo(resumePosition)
            }

            exoPlayer?.play()


            binding.playerView.isVisible = true
            toggleControls()
            updateUi(selectedEpisode)
        }
    }

    private fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                binding.ivPlayPause.setImageResource(R.drawable.svg_play) // your play icon
                binding.bottomBar.ivPlayPauseBottombar.setImageResource(R.drawable.svg_play)
                binding.ivPlayPause.isVisible = true
                binding.ivPlayPause.requestFocus()
                setControlsVisible(true)
            } else {
                player.play()
                binding.ivPlayPause.setImageResource(R.drawable.svg_pause) // your pause icon
                binding.bottomBar.ivPlayPauseBottombar.setImageResource(R.drawable.svg_pause) // your pause icon
                binding.ivPlayPause.isVisible = true
                lifecycleScope.launch {
                    delay(1000)
                    binding.ivPlayPause.isVisible = false
                }
            }
        }
    }
    //endregion

    //region UI Updates
    private fun updateUi(episode: EpisodeDomain) {
        val isLast = isLastEpisode()
        binding.bottomBar.ivPlayNext.isVisible = !isLast
        if (!isLast) {
            readyNextEpisode()
        }
        binding.topBar.tvEpisodeDetail.text = episode.name
    }
    //endregion

    //region releasePlayer
    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
        Log.d(tag, "ExoPlayer released")
    }

    override fun onStop() {
        super.onStop()
        controlsHandler.removeCallbacks(hideRunnable)
        releasePlayer()
    }
    //endregion

    //region onClick
    override fun onClick(view: View?) {
        setControlsVisible(true)
        startControlsAutoHideTimer()
        view?.let {
            when (it) {
                binding.topBar.ivBack -> onBackButtonClicked()
                binding.topBar.ivSource -> changeSourceDialog()
                binding.ivPlayPause, binding.bottomBar.ivPlayPauseBottombar -> togglePlayPause()
                binding.bottomBar.ivBackward -> back10Seconds()
                binding.bottomBar.ivForward -> forward10Seconds()
                binding.bottomBar.ivPlayNext -> playNextVideo()
            }
        }
    }
    //endregion

    //region playNextVideo
    private fun playNextVideo() {
        if (!nextSourcesList.isEmpty()) {
            sourceList = nextSourcesList
            selectedEpisode = nextEpisode
            resumePosition = 0L
            startPlayer(selectedEpisode, preferredSource, resumePosition)
        } else {
            ErrorHandler.show(ctx, SOMETHING_WENT_WRONG)
        }
    }
    //endregion

    //region back and forward 10Seconds
    private fun back10Seconds() {
        exoPlayer?.let { player ->
            val newPosition = player.currentPosition - 10_000L // 10 seconds
            player.seekTo(maxOf(newPosition, 0L)) // prevent going below 0
        }
    }

    private fun forward10Seconds() {
        exoPlayer?.let { player ->
            val newPosition = player.currentPosition + 10_000L // 10 seconds
            val duration = player.duration
            player.seekTo(minOf(newPosition, duration)) // prevent going past video end
        }
    }
    //endregion

    //region changeSourceDialog
    private fun changeSourceDialog() {
        val options: MutableList<Options> = arrayListOf()
        sourceList.forEach {
            options.add(
                Options(
                    buttonTitle = it.videoTitle,
                    isSelected = false,
                ),
            )
        }
        options[preferredSource].isSelected = true
        val radioButtonDialogModel = RadioButtonDialogModel(
            title = "Change source",
            description = "Set your preferred video source",
            options = options,
        )
        val dialog = RadioButtonDialog(
            radioButtonDialogModel = radioButtonDialogModel,
            { position ->
                preferredSource = position
                resumePosition = exoPlayer?.currentPosition ?: 0L
                releasePlayer()
                startPlayer(selectedEpisode, preferredSource, resumePosition)
            },
            {

            },
        )
        dialog.show(supportFragmentManager, "change_source")

    }
    //endregion

    //region onBackButtonClicked
    private fun onBackButtonClicked() {
        finish()
    }
    //endregion

    //region autohide controls
    private fun toggleControls() {
        if (binding.topBar.root.isVisible) {
            setControlsVisible(false)
        } else {
            setControlsVisible(true)
            startControlsAutoHideTimer()
        }
    }

    private fun startControlsAutoHideTimer() {
        controlsHandler.removeCallbacks(hideRunnable)
        controlsHandler.postDelayed(hideRunnable, 5000) // 5 sec
    }


    private fun setControlsVisible(visible: Boolean) {
        val topBar = binding.topBar.root
        val bottomBar = binding.bottomBar.root

        if (visible) {
            // --- TOP BAR (slide down into view) ---
            topBar.isVisible = true
            topBar.animate().translationY(0f).setDuration(250).setInterpolator(DecelerateInterpolator())
                .withEndAction { startControlsAutoHideTimer() }.start()

            // --- BOTTOM BAR (slide up into view, reversed) ---
            bottomBar.isVisible = true
            bottomBar.animate().translationY(0f).setDuration(250).setInterpolator(DecelerateInterpolator()).start()

        } else {
            // --- TOP BAR (slide up and hide) ---
            topBar.animate().translationY(-topBar.height.toFloat()).setDuration(250)
                .setInterpolator(DecelerateInterpolator()).withEndAction { topBar.isVisible = false }.start()

            // --- BOTTOM BAR (slide down and hide, reversed) ---
            bottomBar.animate().translationY(bottomBar.height.toFloat()).setDuration(250)
                .setInterpolator(DecelerateInterpolator()).withEndAction { bottomBar.isVisible = false }.start()
        }
    }
    //endregion

    // region DB Operations
    private fun observeEpisodeUpdate() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.episodeDomain.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
                            Log.d(tag, "Episode Domain Empty")
                        }

                        is BaseUiState.Error -> {
                            Log.d(tag, "Episode Domain Error")
                        }

                        BaseUiState.Idle -> {
                            Log.d(tag, "Episode Domain Error")
                        }

                        BaseUiState.Loading -> {
                            Log.d(tag, "Episode Domain Loading")
                        }

                        is BaseUiState.Success<*> -> {
                            Log.d(tag, "Episode Domain Success")
                            canUpdateEpisode = true
                        }
                    }
                }
            }
        }
    }

    private fun getAllEpisodeObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.episodesDomain.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
                            Log.d(tag, "Episodes Domain Empty")
                        }

                        is BaseUiState.Error -> {
                            Log.d(tag, "Episodes Domain Error")
                        }

                        BaseUiState.Idle -> {
                            Log.d(tag, "Episodes Domain Error")
                        }

                        BaseUiState.Loading -> {
                            Log.d(tag, "Episodes Domain Loading")
                        }

                        is BaseUiState.Success -> {
                            Log.d(tag, "Episodes Domain Success")

                            allEpisodeList = state.data
                            updateUi(selectedEpisode)

                        }
                    }
                }
            }
        }
    }

    private fun updateEpisodeWithDb(position: Long, episode: SEpisode, watchState: Int) {
        viewModel.updateEpisodeWithDb(
            animeUrl = anime.url,
            lastWatchTime = position,
            watchState = watchState,
            episode = episode,
        )
    }
    //endregion

    //region Exit Dialog
    private fun showExitDialog() {
        if (exitDialog == null) {
            exitDialog = ExitDialogFragment(
                title = getString(R.string.do_you_want_to_stop_playing),
                onYes = { finish() },
                onDismissListener = { },
            )
        } else {
            exitDialog?.show(supportFragmentManager, "exit_dialog")
        }
    }
    //endregion
}
