package com.justappz.aniyomitv

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_exo_player)

        videoList = intent.getStringExtra(IntentKeys.VIDEO_LIST)?.toVideoList() ?: emptyList()
        Log.d(tag, "videos ${videoList.size}")

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
