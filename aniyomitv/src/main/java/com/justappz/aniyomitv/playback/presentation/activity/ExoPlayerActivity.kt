package com.justappz.aniyomitv.playback.presentation.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.core.util.FocusKeyHandler
import com.justappz.aniyomitv.databinding.ActivityExoPlayerBinding
import eu.kanade.tachiyomi.animesource.model.SerializableVideo.Companion.toVideoList
import eu.kanade.tachiyomi.animesource.model.Video
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Headers
import java.util.Locale

class ExoPlayerActivity : BaseActivity(), View.OnClickListener {

    //region variables
    private lateinit var binding: ActivityExoPlayerBinding
    private val tag = "ExoPlayerActivity"
    private lateinit var sourceList: List<Video>
    private var selectedSourcePosition = 0
    private var animeName: String = ""
    private var nowPlayingPosition = -1
    private var doubleBackToExitPressedOnce = false
    private var exoPlayer: ExoPlayer? = null
    private val backHandler = Handler(Looper.getMainLooper())
    private val controlsHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { setControlsVisible(false) }
    private val progressHandler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    private var seekJob: Job? = null
    //endregion

    //region onCreate
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exo_player)

        // handle intent data
        sourceList = intent.getStringExtra(IntentKeys.SOURCE_LIST)?.toVideoList() ?: emptyList()
        Log.d(tag, "videos ${sourceList.size}")

        // playing position
        nowPlayingPosition = intent.getIntExtra(IntentKeys.NOW_PLAYING, -1)

        // anime name
        animeName = intent.getStringExtra(IntentKeys.ANIME_NAME).toString()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (doubleBackToExitPressedOnce) {
                        releasePlayer()
                        finish()
                    } else {
                        binding.bottomBar.seekBar.requestFocus()
                        setControlsVisible(true)
                        doubleBackToExitPressedOnce = true
                        Toast.makeText(this@ExoPlayerActivity, "Press back again to exit", Toast.LENGTH_SHORT).show()

                        backHandler.postDelayed({ doubleBackToExitPressedOnce = false }, 2000) // 2s window
                    }
                }
            },
        )

        init()
        startPlayer(nowPlayingPosition, selectedSourcePosition)

    }
    //endregion

    //region init
    private fun init() {
        binding.topBar.ivBack.setOnClickListener(this)

        val tintList = ContextCompat.getColorStateList(ctx, R.color.player_icon_selector)
        ImageViewCompat.setImageTintList(binding.topBar.ivBack, tintList)
        ImageViewCompat.setImageTintList(binding.ivPlayPause, tintList)

        binding.playerView.setOnKeyListener(
            FocusKeyHandler(
                onCenter = {
                    togglePlayPause()
                    return@FocusKeyHandler true
                },
            ),
        )

        binding.ivPlayPause.setOnKeyListener(
            FocusKeyHandler(
                onCenter = {
                    togglePlayPause()
                    return@FocusKeyHandler true
                }
            )
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
            ),
        )
    }
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
                        tvTotal.text = formatTime(exoPlayer?.duration ?: 0L)
                        seekBar.max = (exoPlayer?.duration ?: 0L).toInt()
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
        seekBar.setOnFocusChangeListener { _, hasFocus ->
            isUserSeeking = hasFocus
            if (!hasFocus) {
                // Leaving focus → just let idle job finish
                cancelSeekJob()
            }
        }

        // Progress updater
        val updateProgress = object : Runnable {
            override fun run() {
                if (!isUserSeeking && exoPlayer?.isPlaying == true) {
                    val position = exoPlayer?.currentPosition ?: 0L
                    seekBar.progress = position.toInt()
                    tvCurrent.text = formatTime(position)
                }
                progressHandler.postDelayed(this, 500)
            }
        }
        progressHandler.post(updateProgress)
    }

    private fun startSeekIdleJob(position: Long) {
        cancelSeekJob()
        seekJob = lifecycleScope.launch {
            delay(500) // wait 1s of inactivity
            seekToPosition(position)
            isUserSeeking = false
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
    private fun startPlayer(nowPlayingPosition: Int, selectedSourcePosition: Int) {
        val video = sourceList[selectedSourcePosition]
        // Create a DataSource.Factory with custom headers
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(video.headers?.toMap() ?: emptyMap())

        // Create a MediaItem
        val mediaItem = MediaItem.fromUri(video.videoUrl)

        // Initialize ExoPlayer
        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()

        // UI operations must be on the main thread
        runOnUiThread {
            // Bind to PlayerView
            binding.playerView.player = exoPlayer

            // Set media and start playback
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
            binding.playerView.isVisible = true
            toggleControls()
            initSeekBar()
        }
    }

    private fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                binding.ivPlayPause.setImageResource(R.drawable.svg_play) // your play icon
                binding.ivPlayPause.isVisible = true
                binding.ivPlayPause.requestFocus()
                setControlsVisible(true)
            } else {
                player.play()
                binding.ivPlayPause.setImageResource(R.drawable.svg_pause) // your pause icon
                binding.ivPlayPause.isVisible = true
                lifecycleScope.launch {
                    delay(1000)
                    binding.ivPlayPause.isVisible = false
                }
            }
        }
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
        view?.let {
            when (it) {
                binding.topBar.ivBack -> onBackButtonClicked()
            }
        }
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
        controlsHandler.postDelayed(hideRunnable, 3000) // 3 sec
    }


    private fun setControlsVisible(visible: Boolean) {
        if (isUserSeeking) return
        if (exoPlayer?.isPlaying == false) return
        val topBar = binding.topBar.root
        val bottomBar = binding.bottomBar.root

        if (visible) {
            // --- TOP BAR (slide down into view) ---
            topBar.isVisible = true
            topBar.animate()
                .translationY(0f)
                .setDuration(250)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { startControlsAutoHideTimer() }
                .start()

            // --- BOTTOM BAR (slide up into view, reversed) ---
            bottomBar.isVisible = true
            bottomBar.animate()
                .translationY(0f)
                .setDuration(250)
                .setInterpolator(DecelerateInterpolator())
                .start()

        } else {
            // --- TOP BAR (slide up and hide) ---
            topBar.animate()
                .translationY(-topBar.height.toFloat())
                .setDuration(250)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { topBar.isVisible = false }
                .start()

            // --- BOTTOM BAR (slide down and hide, reversed) ---
            bottomBar.animate()
                .translationY(bottomBar.height.toFloat())
                .setDuration(250)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { bottomBar.isVisible = false }
                .start()
        }
    }
    //endregion
}
