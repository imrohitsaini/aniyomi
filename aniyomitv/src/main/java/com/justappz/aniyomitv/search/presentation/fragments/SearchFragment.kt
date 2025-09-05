package com.justappz.aniyomitv.search.presentation.fragments

import android.annotation.SuppressLint
import android.content.Intent
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
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.constants.IntentKeys
import com.justappz.aniyomitv.constants.PrefsKeys
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.chips.ChipView
import com.justappz.aniyomitv.core.components.decoration.GridSpacingItemDecoration
import com.justappz.aniyomitv.core.components.dialog.RadioButtonDialog
import com.justappz.aniyomitv.core.error.ErrorHandler
import com.justappz.aniyomitv.core.model.Options
import com.justappz.aniyomitv.core.model.RadioButtonDialogModel
import com.justappz.aniyomitv.core.util.PrefsManager
import com.justappz.aniyomitv.databinding.FragmentSearchBinding
import com.justappz.aniyomitv.episodes.presentation.activity.EpisodesActivity
import com.justappz.aniyomitv.search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.search.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.search.domain.usecase.GetLatestAnimePagingUseCase
import com.justappz.aniyomitv.search.domain.usecase.GetPopularAnimePagingUseCase
import com.justappz.aniyomitv.search.presentation.adapters.AnimePagingAdapter
import com.justappz.aniyomitv.search.presentation.viewmodel.SearchViewModel
import eu.kanade.tachiyomi.animesource.model.SAnime
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

    private val popularChip = 0
    private val latestChip = 1
    private var firstLoadHandled = false
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


        setupAnimeAdapter()

        observeInstalledExtensions()
        viewModel.getExtensions(ctx)

        availableChips.add(0, binding.chipPopular)
        availableChips.add(1, binding.chipLatest)

        binding.chipPopular.setOnClickListener(this)
        binding.chipLatest.setOnClickListener(this)
        binding.tvChangeSource.setOnClickListener(this)
    }
    //endregion

    //region setupAnimeAdapter
    @SuppressLint("SetTextI18n")
    private fun setupAnimeAdapter() {
        // setup adapter
        val spacing = ctx.resources.getDimensionPixelSize(R.dimen._16dp)
        binding.rvAnime.layoutManager = GridLayoutManager(ctx, 5)
        binding.rvAnime.adapter = animeAdapter
        binding.rvAnime.addItemDecoration(GridSpacingItemDecoration(5, spacing))
        animeAdapter.attachRecyclerView(binding.rvAnime)

        animeAdapter.onItemClick = { anime, position ->
            startEpisodesActivity(anime)
        }


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
                            val isEmpty = animeAdapter.itemCount == 0
                            binding.errorRoot.root.isVisible = isEmpty
                            if (isEmpty) {
                                binding.errorRoot.tvError.text = getString(R.string.no_data_found)
                            } else {
                                // ✅ Only reset to top on very first load
                                if (!firstLoadHandled) {
                                    firstLoadHandled = true
                                    binding.rvAnime.scrollToPosition(0)
                                    binding.rvAnime.post {
                                        val vh = binding.rvAnime.findViewHolderForAdapterPosition(0)
                                        vh?.itemView?.requestFocus()
                                    }
                                }
                            }
                        }

                        is androidx.paging.LoadState.Error -> {
                            showLoading(false)
                            binding.errorRoot.root.isVisible = true
                            binding.rvAnime.isVisible = false
                            binding.errorRoot.tvError.text = getString(R.string.no_data_found)
                        }
                    }
                }
            }
        }

        animeAdapter.addLoadStateListener {
            Log.d("AnimeAdapter", "Item count: ${animeAdapter.itemCount}")
        }
    }
    //endregion

    //region startEpisodesActivity
    private fun startEpisodesActivity(anime: SAnime) {
        val extensionInfo = installedExtensions.firstOrNull { it.isSelected == true }
        startActivity(
            Intent(act, EpisodesActivity::class.java).apply {
                putExtra(IntentKeys.ANIME, anime)
                putExtra(IntentKeys.ANIME_CLASS, extensionInfo?.className)
                putExtra(IntentKeys.ANIME_PKG, extensionInfo?.packageName)
            },
        )
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
                            ErrorHandler.show(ctx, extensionsState.error, binding.errorRoot.tvError)
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
                            // set the ui
                            binding.errorRoot.root.isVisible = false
                            binding.chipPopular.isVisible = true
                            binding.chipLatest.isVisible = true

                            val preferredExtensionName = PrefsManager.getString(PrefsKeys.PREFERRED_EXTENSION, null)

                            var selectionIndex = 0
                            if (preferredExtensionName != null) {
                                selectionIndex = extensions.indexOfFirst { it.appName == preferredExtensionName }
                            }

                            if (selectionIndex == -1) {
                                selectionIndex = 0
                                PrefsManager.putString(
                                    PrefsKeys.PREFERRED_EXTENSION,
                                    extensions[selectionIndex].appName,
                                )
                            }

                            // take first installed extension
                            selectedAnimeSource = extensions[selectionIndex].instance

                            installedExtensions.addAll(extensions)
                            installedExtensions[selectionIndex].isSelected = true

                            binding.chipLatest.isVisible = selectedAnimeSource?.supportsLatest == true

                            binding.tvChangeSource.isVisible = installedExtensions.size >= 2

                            // After changing the extension, always load popular anime
                            selectChip(popularChip)
                            viewModel.resetExtensionState()
                        }

                        BaseUiState.Empty -> {
                            showLoading(false)
                            binding.errorRoot.tvError.text = "No extensions detected"
                            binding.errorRoot.root.isVisible = true
                            binding.chipPopular.isVisible = false
                            binding.chipLatest.isVisible = false
                            binding.tvChangeSource.isVisible = false
                        }
                    }

                }
            }
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
                    selectChip(popularChip)
                }

                binding.chipLatest -> {
                    selectChip(latestChip)
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
        firstLoadHandled = false
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
            description = "Select your preferred extension!",
            options = options,
        )
        val dialog = RadioButtonDialog(
            radioButtonDialogModel,
            onDone = { position ->
                installedExtensions.forEach {
                    it.isSelected = false
                }
                installedExtensions[position].isSelected = true
                val selected = installedExtensions.firstOrNull { it.isSelected == true }
                selectedAnimeSource = selected?.instance
                binding.chipLatest.isVisible = selectedAnimeSource?.supportsLatest == true
                selectChip(popularChip)

                // Save this as preference for next time
                selected?.let {
                    PrefsManager.putString(PrefsKeys.PREFERRED_EXTENSION, it.appName)
                }
            },
            onDismissListener = {},
        )
        dialog.show(parentFragmentManager, "options_dialog")
    }
    //endregion
}
