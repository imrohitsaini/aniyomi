package com.justappz.aniyomitv

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.databinding.ActivityExoPlayerBinding
import eu.kanade.tachiyomi.animesource.model.SerializableVideo.Companion.toVideoList
import eu.kanade.tachiyomi.animesource.model.Video
import okhttp3.Headers

class ExoPlayerActivity : BaseActivity() {

    //region variables
    private lateinit var binding: ActivityExoPlayerBinding
    private val tag = "ExoPlayerActivity"
    private lateinit var videoList: List<Video>
    private var doubleBackToExitPressedOnce = false
    private var exoPlayer: ExoPlayer? = null
    private val backHandler = Handler(Looper.getMainLooper())
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exo_player)

        videoList = intent.getStringExtra(IntentKeys.VIDEO_LIST)?.toVideoList() ?: emptyList()
        Log.d(tag, "videos ${videoList.size}")


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

        startPlayer(videoList[0])

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
    private fun startPlayer(video: Video) {
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
        releasePlayer()
    }
    //endregion
}
