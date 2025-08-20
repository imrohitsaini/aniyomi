package com.justappz.aniyomitv

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.databinding.ActivityPlayerBinding
import com.justappz.aniyomitv.util.toObject
import eu.kanade.tachiyomi.animesource.model.Video
import okhttp3.Headers


class PlayerActivity : AppCompatActivity() {

    //region variables
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var activity: Activity
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var video: Video? = null
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activity = this
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        var videoObjStr = intent.getStringExtra(IntentKeys.VIDEO_OBJ)
        video = videoObjStr?.toObject()

        playerView = binding.playerView

        video?.let {
            initializePlayer(it)
        } ?: run {
            // Handle the case where streamUrl is null
            Log.e("PlayerActivity", "Stream URL is null")
        }
    }
    //endregion

    //region onStop
    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
    //endregion

    fun Headers.toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (i in 0 until size) {
            map[name(i)] = value(i)
        }
        return map
    }


    //region initializePlayer
    @androidx.annotation.OptIn(UnstableApi::class)
    private fun initializePlayer(video: Video) {

        // Create a DataSource.Factory with custom headers
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(video.headers?.toMap() ?: emptyMap())

        // Create a MediaItem
        val mediaItem = MediaItem.fromUri(video.videoUrl)

        // Initialize ExoPlayer
        val exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()

        // UI operations must be on the main thread
        runOnUiThread {
            // Bind to PlayerView
            binding.playerView.player = exoPlayer

            // Set media and start playback
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()

        }
    }
    //endregion

}
