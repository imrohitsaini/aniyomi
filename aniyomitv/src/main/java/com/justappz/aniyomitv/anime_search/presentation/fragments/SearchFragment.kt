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
import com.justappz.aniyomitv.anime_search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.anime_search.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.anime_search.domain.usecase.GetLatestAnimePagingUseCase
import com.justappz.aniyomitv.anime_search.domain.usecase.GetPopularAnimePagingUseCase
import com.justappz.aniyomitv.anime_search.presentation.adapters.AnimePagingAdapter
import com.justappz.aniyomitv.anime_search.presentation.viewmodel.SearchViewModel
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.chips.ChipView
import com.justappz.aniyomitv.core.components.decoration.GridSpacingItemDecoration
import com.justappz.aniyomitv.core.components.dialog.RadioButtonDialog
import com.justappz.aniyomitv.core.model.Options
import com.justappz.aniyomitv.core.model.RadioButtonDialogModel
import com.justappz.aniyomitv.core.util.toJson
import com.justappz.aniyomitv.databinding.FragmentSearchBinding
import dalvik.system.PathClassLoader
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class SearchFragment : BaseFragment(), View.OnClickListener {

    //region variables
    private val tag = "SearchFragment"
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels {
        ViewModelFactory {
            SearchViewModel(
                Injekt.get<GetInstalledExtensionsUseCase>(),
                Injekt.get<GetPopularAnimePagingUseCase>(),
                Injekt.get<GetLatestAnimePagingUseCase>(),
            )
        }
    }
    private val animeAdapter = AnimePagingAdapter()
    private var availableChips: MutableList<ChipView> = arrayListOf()
    private var selectedAnimeSource: AnimeHttpSource? = null
    private var installedExtensions: MutableList<InstalledExtensions> = arrayListOf()
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                animeAdapter.loadStateFlow.collect { loadStates ->
                    val refreshState = loadStates.refresh

                    when (refreshState) {
                        is androidx.paging.LoadState.Loading -> {
                            // First load or refresh
                            showLoading(true)
                            binding.errorRoot.root.isVisible = false
                        }

                        is androidx.paging.LoadState.NotLoading -> {
                            showLoading(false)

                            // Check if adapter is empty after load completes
                            val isEmpty = animeAdapter.itemCount == 0
                            binding.errorRoot.root.isVisible = isEmpty
                            if (isEmpty) {
                                binding.errorRoot.tvError.text = "No anime found"
                            }
                        }

                        is androidx.paging.LoadState.Error -> {
                            showLoading(false)
                            binding.errorRoot.root.isVisible = true
                            binding.errorRoot.tvError.text =
                                refreshState.error.message ?: "Something went wrong"
                        }
                    }
                }
            }
        }

        animeAdapter.addLoadStateListener {
            Log.d("AnimeAdapter", "Item count: ${animeAdapter.itemCount}")
        }

        observeInstalledExtensions()
        viewModel.getExtensions(ctx)

        availableChips.add(0, binding.chipPopular)
        availableChips.add(1, binding.chipLatest)

        binding.chipPopular.setOnClickListener(this)
        binding.chipLatest.setOnClickListener(this)
        binding.tvChangeSource.setOnClickListener(this)
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
                        is BaseUiState.Error -> {
                            showLoading(false)
                        }

                        BaseUiState.Idle -> {
                            showLoading(false)
                        }

                        BaseUiState.Loading -> {
                            showLoading(true)
                        }

                        is BaseUiState.Success -> {
                            val extensions = extensionsState.data
                            Log.d(tag, "installed extensions ${extensions.toJson()}")
                            // set the ui
                            binding.errorRoot.root.isVisible = false

                            // take first available extension that has instance
                            selectedAnimeSource = extensions.firstOrNull()?.instance

                            installedExtensions.addAll(extensions)
                            installedExtensions[0].isSelected = true

                            // After changing the extension, always load popular anime
                            selectChip(0)
                        }

                        BaseUiState.Empty -> {
                            showLoading(false)
                            binding.errorRoot.tvError.text = "No extensions detected"
                            binding.errorRoot.root.isVisible = true
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
        lifecycleScope.launch(Dispatchers.Main) {
            binding.loading.isVisible = toShow
            Log.d(tag, "loader $toShow")
        }
    }
    //endregion

    //region onClick
    override fun onClick(view: View?) {
        Log.d(tag, "onClick")
        view?.let {
            when (view) {
                binding.chipPopular -> {
                    selectChip(0)
                }

                binding.chipLatest -> {
                    selectChip(1)
                }

                binding.tvChangeSource -> {
                    changeSourceDialog()
                }
            }
        }
    }
    //endregion

    //region selectChip
    private fun selectChip(position: Int) {
        Log.d(tag, "selectChip")
        val chip = availableChips[position]
        availableChips.forEach { it.setSelectedState(false) }
        chip.setSelectedState(true)
        if (chip.getText().contains("popular", true)) {
            // load popular anime
            Log.d(tag, "loadPopularAnime from chip")
            selectedAnimeSource?.let { loadPopularAnime(it) }
        } else if (chip.getText().contains("latest", true)) {
            // load latest anime
            Log.d(tag, "loadLatestAnime from chip")
            selectedAnimeSource?.let { loadLatestAnime(it) }
        }
    }
    //endregion

    //region loadPopularAnime
    private fun loadPopularAnime(source: AnimeHttpSource) {
        binding.rvAnime.isVisible = true
        Log.d(tag, "loadPopularAnime")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPopularAnime(source).collect { pagingData ->
                    Log.d(tag, "Submitting popular data to adapter")
                    animeAdapter.submitData(pagingData)
                }
            }
        }
    }
    //endregion


    //region loadLatestAnime
    private fun loadLatestAnime(source: AnimeHttpSource) {
        Log.d(tag, "loadLatestAnime")
        binding.rvAnime.isVisible = true
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getLatestAnime(source).collect { pagingData ->
                    Log.d(tag, "Submitting latest data to adapter")
                    animeAdapter.submitData(pagingData)
                }
            }
        }
    }
    //endregion

    //region changeSourceDialog
    private fun changeSourceDialog() {
        val options: MutableList<Options> = arrayListOf()
        installedExtensions.forEach { extension ->
            options.add(
                Options(
                    buttonTitle = extension.appName,
                    isSelected = extension.isSelected,
                ),
            )
        }
        val radioButtonDialogModel = RadioButtonDialogModel(
            title = getString(R.string.select_extensions),
            description = "Select source for anime!",
            options = options,
            isDefaultSelected = true,
        )
        val dialog = RadioButtonDialog(
            radioButtonDialogModel,
            onDone = { option ->
                var selected: InstalledExtensions? = null
                installedExtensions.forEach {
                    val match = it.appName == option.buttonTitle
                    it.isSelected = match
                    if (match) selected = it
                }
                selectedAnimeSource = selected?.instance
                selectChip(0)
            },
            onDismissListener = {},
        )
        dialog.show(parentFragmentManager, "options_dialog")
    }
    //endregion
}
