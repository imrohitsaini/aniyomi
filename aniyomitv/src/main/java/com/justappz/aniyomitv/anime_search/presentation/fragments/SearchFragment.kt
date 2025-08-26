package com.justappz.aniyomitv.anime_search.presentation.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.anime_search.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.anime_search.domain.usecase.GetPopularAnimePagingUseCase
import com.justappz.aniyomitv.anime_search.presentation.adapters.AnimePagingAdapter
import com.justappz.aniyomitv.anime_search.presentation.states.GetInstalledExtensionsState
import com.justappz.aniyomitv.anime_search.presentation.viewmodel.SearchViewModel
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.decoration.GridSpacingItemDecoration
import com.justappz.aniyomitv.core.util.toJson
import com.justappz.aniyomitv.databinding.FragmentSearchBinding
import com.justappz.aniyomitv.databinding.ItemAnimeBinding
import dalvik.system.PathClassLoader
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class SearchFragment : BaseFragment() {

    //region variables
    private val tag = "SearchFragment"
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels {
        ViewModelFactory {
            SearchViewModel(
                Injekt.get<GetInstalledExtensionsUseCase>(),
                Injekt.get<GetPopularAnimePagingUseCase>(),
            )
        }
    }
    private val animeAdapter = AnimePagingAdapter()

    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d(tag, "onCreateView")
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    //endregion

    //region onDestroyView
    override fun onDestroyView() {
        Log.d(tag, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(tag, "onViewCreated")

        init()
    }
    //endregion

    //region init
    private fun init() {
        Log.d(tag, "init")

        // setup adapter
        val spacing = ctx.resources.getDimensionPixelSize(R.dimen._16dp)
        binding.rvAnime.layoutManager = GridLayoutManager(ctx, 5)
        binding.rvAnime.adapter = animeAdapter
        binding.rvAnime.addItemDecoration(GridSpacingItemDecoration(5, spacing))
        animeAdapter.attachRecyclerView(binding.rvAnime)

        observeInstalledExtensions()
        viewModel.getExtensions(ctx)
        showLoading(true)
    }
    //endregion

    //region observeInstalledExtensions
    @SuppressLint("SetTextI18n")
    private fun observeInstalledExtensions() {
        Log.d(tag, "observeInstalledExtensions")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.extensionState.collect { extensionsState ->
                    when (extensionsState) {
                        is GetInstalledExtensionsState.Error -> {
                            showLoading(false)
                        }

                        GetInstalledExtensionsState.Idle -> {
                            showLoading(false)
                        }

                        GetInstalledExtensionsState.Loading -> {
                            showLoading(true)
                        }

                        is GetInstalledExtensionsState.Success -> {
                            showLoading(false)
                            val extensions = extensionsState.installedExtensions
                            Log.d(tag, "installed extensions ${extensions.toJson()}")
                            if (extensions.isEmpty()) {
                                binding.errorRoot.tvError.text = "No extensions detected"
                                binding.errorRoot.root.isVisible = true
                            } else {
                                // set the ui
                                binding.errorRoot.root.isVisible = false

                                // take first available extension that has instance
                                val source = extensions.firstOrNull()?.instance
                                if (source != null) {
                                    // collect paging data
                                    lifecycleScope.launch {
                                        viewModel.getPopularAnime(source).collect { pagingData ->
                                            animeAdapter.submitData(pagingData)
                                        }
                                    }
                                }
                            }
                            viewModel.resetExtensionState()
                        }
                    }

                }
            }
        }
    }
    //endregion

    //region detectExtensions
    private fun detectExtensions() {
        try {
            val pm = act.packageManager
            val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
            val extensions = packages.filter { pkg ->
                val info =
                    pm.getPackageInfo(pkg.packageName, PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES)
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
                act.classLoader,
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

    //region Show Loading
    private fun showLoading(toShow: Boolean) {
        binding.loading.isVisible = toShow
    }
    //endregion
}
