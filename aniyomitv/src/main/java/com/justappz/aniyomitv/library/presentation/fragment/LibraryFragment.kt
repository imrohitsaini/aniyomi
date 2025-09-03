package com.justappz.aniyomitv.library.presentation.fragment

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
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.decoration.GridSpacingItemDecoration
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.core.error.ErrorHandler
import com.justappz.aniyomitv.databinding.FragmentHomeBinding
import com.justappz.aniyomitv.episodes.presentation.activity.EpisodesActivity
import com.justappz.aniyomitv.library.presentation.adapter.AnimeLibraryAdapter
import com.justappz.aniyomitv.library.presentation.viewmodel.LibraryViewModel
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain
import com.justappz.aniyomitv.playback.domain.model.toSAnime
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class LibraryFragment : BaseFragment() {

    //region variables
    private val tag = "SearchFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var animeLibraryAdapter: AnimeLibraryAdapter

    private val viewModel: LibraryViewModel by viewModels {
        ViewModelFactory {
            LibraryViewModel(
                Injekt.get(),
            )
        }
    }
    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d(tag, "onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        setAdapterProperties()
        observeAnime()
        viewModel.getAnimeInLibrary()
    }
    //endregion

    //region Anime Adapter
    private fun setAdapterProperties() {
        animeLibraryAdapter = AnimeLibraryAdapter(emptyList())
        animeLibraryAdapter.onItemClick = { anime, _ -> startEpisodesActivity(anime) }
        val spacing = ctx.resources.getDimensionPixelSize(R.dimen._16dp)
        binding.rvAnime.layoutManager = GridLayoutManager(ctx, 5)
        binding.rvAnime.adapter = animeLibraryAdapter
        binding.rvAnime.addItemDecoration(GridSpacingItemDecoration(5, spacing))
        animeLibraryAdapter.attachRecyclerView(binding.rvAnime)
    }
    //endregion

    //region observeAnime
    private fun observeAnime() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animeDomain.collect { state ->
                    when (state) {
                        BaseUiState.Empty -> {
                            showLoading(false)
                            binding.errorRoot.root.isVisible = true
                            binding.rvAnime.isVisible = false
                            ErrorHandler.show(
                                context = ctx,
                                error = AppError.UnknownError(
                                    message = "Add anime in library",
                                    displayType = ErrorDisplayType.INLINE,
                                ),
                                inlineView = binding.errorRoot.tvError,
                            )
                        }

                        is BaseUiState.Error -> {
                            showLoading(false)
                        }

                        BaseUiState.Idle -> {
                        }

                        BaseUiState.Loading -> {
                            showLoading(true)
                        }

                        is BaseUiState.Success -> {
                            val animeList = state.data

                            animeLibraryAdapter.updateList(animeList)

                            binding.errorRoot.root.isVisible = false
                            binding.rvAnime.isVisible = true

                            showLoading(false)
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region showLoading
    private fun showLoading(toShow: Boolean) {
        binding.loading.isVisible = toShow
    }
    //endregion

    //region startEpisodesActivity
    private fun startEpisodesActivity(anime: AnimeDomain) {
        startActivity(
            Intent(act, EpisodesActivity::class.java).apply {
                putExtra(IntentKeys.ANIME, anime.toSAnime())
                putExtra(IntentKeys.ANIME_CLASS, anime.className)
                putExtra(IntentKeys.ANIME_PKG, anime.packageName)
            },
        )
    }
    //endregion
}
