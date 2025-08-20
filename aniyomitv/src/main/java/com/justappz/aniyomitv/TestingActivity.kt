package com.justappz.aniyomitv

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.databinding.ActivityMainBinding
import com.justappz.aniyomitv.databinding.ActivityTestingBinding
import com.justappz.aniyomitv.util.toJson
import dalvik.system.PathClassLoader
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestingActivity : AppCompatActivity(), View.OnClickListener {

    //region variables
    private lateinit var binding: ActivityTestingBinding
    private lateinit var activity: Activity
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTestingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }
    //endregion

    //region init
    private fun init() {
        binding.btnDetectExtensions.setOnClickListener(this)
    }
    //endregion

    //region onClick
    override fun onClick(v: View?) {
        v?.let {
            if (v == binding.btnDetectExtensions) {
                v.isEnabled = false
                binding.progressbarLoading.visibility = View.VISIBLE
                detectExtensions()
                v.isEnabled = true
            }
        }
    }
    //endregion

    //region detectExtensions
    private fun detectExtensions() {
        try {
            val pm = activity.packageManager
            val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
            val extensions = packages.filter { pkg ->
                val info = pm.getPackageInfo(pkg.packageName, PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES)
                info.applicationInfo?.metaData?.containsKey("tachiyomi.animeextension.class") == true
            }
            val extInfo = extensions[0] // your detected extension
            val meta = extInfo.applicationInfo?.metaData
            val className = meta?.getString("tachiyomi.animeextension.class")
            val nsfwFlag = meta?.getInt("tachiyomi.animeextension.nsfw", 0) ?: 0
            val pkgName = extInfo.packageName
            val fullClassName = if (className!!.startsWith(".")) pkgName + className else className

            Log.d("Extension", "Class: $fullClassName, NSFW: $nsfwFlag")

            val pathLoader = PathClassLoader(
                extInfo.applicationInfo?.sourceDir,
                activity.classLoader
            )

            val clazz = pathLoader.loadClass(fullClassName)
            val instance = clazz.getDeclaredConstructor().newInstance()
            Log.d("Extension Instance", "Instance: $instance")
            if (instance is AnimeHttpSource) {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                         // Step 1: Get popular anime
                        val page = instance.getPopularAnime(1)
                        val anime = page.animes.first()

                        // Step 2: Get episode list
                        val episodes = instance.getEpisodeList(anime)
                        val episode = episodes.first()

                        // Step 3: Get videos (streams)
                        val videos = instance.getVideoList(episode)

                        for (video in videos) {
                            Log.d("Stream", "Quality: ${video.videoTitle}, Url: ${video.videoUrl}")
                        }

                        // Play first stream
                        val streamUrl = videos.first().videoUrl
                        Log.e("Stream", "Playing first stream: $streamUrl")

                        val firstVideo = videos.first()
                        val firstVideoStr = firstVideo.toJson()

                        binding.progressbarLoading.visibility = View.GONE
                        startActivity(Intent(activity, PlayerActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putExtra(IntentKeys.VIDEO_OBJ, firstVideoStr)
                        })

                    } catch (e: Exception) {
                        Log.e("Extension", "Error fetching popular anime", e)
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error detecting extensions", e)
        }
    }
    //endregion
}
