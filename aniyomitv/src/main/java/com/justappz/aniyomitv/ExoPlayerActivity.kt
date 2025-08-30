package com.justappz.aniyomitv

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.databinding.ActivityExoPlayerBinding
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.animesource.model.SerializableVideo.Companion.toVideoList
import eu.kanade.tachiyomi.animesource.model.Video
import okhttp3.Headers

class ExoPlayerActivity : BaseActivity(), View.OnClickListener {

    //region variables
    private lateinit var binding: ActivityExoPlayerBinding
    private val tag = "ExoPlayerActivity"
    private lateinit var sourceList: List<Video>
    private var selectedSourcePosition = 0
    private var animeName: String = ""
    private var episodeList: ArrayList<SEpisode>? = arrayListOf()
    private var nowPlayingPosition = -1
    private var doubleBackToExitPressedOnce = false
    private var exoPlayer: ExoPlayer? = null
    private val backHandler = Handler(Looper.getMainLooper())
    private val controlsHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { setControlsVisible(false) }
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

        //episode list
        @Suppress("UNCHECKED_CAST")
        episodeList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(IntentKeys.EPISODE_LIST, ArrayList::class.java) as? ArrayList<SEpisode>
        } else {
            intent.getSerializableExtra(IntentKeys.EPISODE_LIST) as? ArrayList<SEpisode>
        }

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

        binding.playerView.setOnClickListener {
            toggleControls()
        }
    }
    //endregion

    //region setHeading
    private fun setHeading(episode: SEpisode) {
        binding.topBar.tvAnimeTitle.text = animeName
        binding.topBar.tvEpisodeDetail.text = episode.name
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
            episodeList?.let {
                setHeading(it[nowPlayingPosition])
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
        val topBar = binding.topBar.root

        if (visible) {
            // Ensure it's visible before animating
            topBar.isVisible = true
            topBar.animate()
                .translationY(0f) // slide down into view
                .setDuration(250) // short & snappy
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { startControlsAutoHideTimer() }
                .start()
        } else {
            topBar.animate()
                .translationY(-topBar.height.toFloat()) // slide up
                .setDuration(250)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { topBar.isVisible = false }
                .start()
        }
    }
    //endregion
}
