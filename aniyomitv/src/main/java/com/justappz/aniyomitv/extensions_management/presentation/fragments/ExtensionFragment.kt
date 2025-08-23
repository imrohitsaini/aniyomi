package com.justappz.aniyomitv.extensions_management.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.dialog.InputDialogFragment
import com.justappz.aniyomitv.core.util.DisplayUtils
import com.justappz.aniyomitv.core.util.UrlUtils
import com.justappz.aniyomitv.core.util.toJsonArray
import com.justappz.aniyomitv.databinding.FragmentExtensionBinding
import com.justappz.aniyomitv.extensions_management.domain.model.AnimeRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.model.Chip
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetRepoUrlsUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.RemoveRepoUrlUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.SaveRepoUrlUseCase
import com.justappz.aniyomitv.extensions_management.presentation.adapters.ExtensionPagingAdapter
import com.justappz.aniyomitv.extensions_management.presentation.adapters.RepoChipsAdapter
import com.justappz.aniyomitv.extensions_management.presentation.states.ExtensionsUiState
import com.justappz.aniyomitv.extensions_management.presentation.states.RepoUiState
import com.justappz.aniyomitv.extensions_management.presentation.viewmodel.ExtensionViewModel
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ExtensionFragment : BaseFragment() {

    //region variables
    private var _binding: FragmentExtensionBinding? = null
    private val binding get() = _binding!!
    private val tag = "ExtensionFragment"
    private var isDialogShowing = false
    private val extensionViewModel: ExtensionViewModel by viewModels {
        ViewModelFactory {
            ExtensionViewModel(
                Injekt.get<GetExtensionUseCase>(),
                Injekt.get<GetRepoUrlsUseCase>(),
                Injekt.get<SaveRepoUrlUseCase>(),
                Injekt.get<RemoveRepoUrlUseCase>(),
            )
        }
    }

    // Need this to not add duplicate repo url
    private lateinit var animeRepos: List<AnimeRepositoriesDetailsDomain>
    private var chips: MutableList<Chip> = arrayListOf()
    private var selectedChip: Chip? = null
    private lateinit var repoUrlChipsAdapter: RepoChipsAdapter
    private var dialog: InputDialogFragment? = null
    private var isNewRepo: Boolean = false

    private var extensionAdapter = ExtensionPagingAdapter()

    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_extension, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
    //endregion

    //region onDestroyView
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }
    //endregion

    //region init()
    private fun init() {

        reposAdapterProperties()
        extensionAdapterProperties()
        observeRepo()
        observeExtensionState()
        extensionViewModel.loadRepoUrls()
    }
    //endregion

    //region reposAdapterProperties()
    private fun reposAdapterProperties() {
        repoUrlChipsAdapter = RepoChipsAdapter(emptyList()).apply {
            onItemClick = { chip, position -> onChipClicked(chip, position) }
        }
        binding.rvRepos.layoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRepos.adapter = repoUrlChipsAdapter
    }

    //region extensionAdapterProperties
    private fun extensionAdapterProperties() {
        // Extension RecyclerView setup
        val spanCount = DisplayUtils.calculateSpanCount(ctx, 150, 5)// Number of columns for TV
        binding.rvExtensions.layoutManager = GridLayoutManager(ctx, spanCount)
        binding.rvExtensions.adapter = extensionAdapter

        // Optional: Focus animation for TV
        binding.rvExtensions.addOnChildAttachStateChangeListener(
            object : RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {
                    view.setOnFocusChangeListener { v, hasFocus ->
                        v.animate()
                            .scaleX(if (hasFocus) 1.1f else 1f)
                            .scaleY(if (hasFocus) 1.1f else 1f)
                            .setDuration(150)
                            .start()
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) {}
            },
        )
    }
    //endregion

    //region observeRepo
    private fun observeRepo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                extensionViewModel.repoUrls.collect { reposState ->
                    when (reposState) {
                        is RepoUiState.Error -> {
                            showLoading(false)
                        }

                        RepoUiState.Idle -> {
                            showLoading(false)
                        }

                        RepoUiState.Loading -> {
                            showLoading(true)
                        }

                        is RepoUiState.Success -> {
                            showLoading(false)
                            animeRepos = reposState.data
                            Log.i(tag, "repoUrls fetched ${animeRepos.toJsonArray()}")

                            updateRepoChips(animeRepos)
                        }
                    }

                }
            }
        }
    }
    //endregion

    //region updateRepoChips
    private fun updateRepoChips(repositoriesDetailsDomains: List<AnimeRepositoriesDetailsDomain>) {
        Log.i(tag, "updateRepoChips()")

        chips.clear()
        Log.i(tag, "chips.clear()")

        repositoriesDetailsDomains.forEach {
            chips.add(
                Chip(
                    url = it.repoUrl,
                    chipName = it.cleanName,
                ),
            )
        }

        // Add the repo chip at last of this
        chips.add(
            Chip(
                url = "",
                chipName = "Add Repo",
                isAddRepoChip = true,
                chipIcon = R.drawable.svg_add,
            ),
        )

        if (chips.size > 1) {   // It has urls
            // select the first chip
            selectedChip = chips[0].apply { isSelected = true }
        }

        Log.i(tag, "updateList()")
        repoUrlChipsAdapter.updateList(chips)


        selectedChip?.let {
            extensionViewModel.loadExtensions(it.url)
        }
    }
    //endregion

    //region observeExtensionState
    private fun observeExtensionState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                extensionViewModel.extensionState.collect { state ->
                    when (state) {
                        ExtensionsUiState.Idle -> {
                            showLoading(false)
                        }

                        is ExtensionsUiState.Loading -> {
                            showLoading(true)
                            binding.errorRoot.root.isVisible = false
                        }

                        is ExtensionsUiState.Success -> {
                            showLoading(false)
                            val repoDomain = state.data
                            Log.i(
                                tag,
                                "extensions successfully fetched ${repoDomain.extensions.size} for url ${repoDomain.repoUrl}",
                            )
                            // Submit list to Paging adapter
                            lifecycleScope.launch {
                                extensionAdapter.submitData(
                                    pagingData = androidx.paging.PagingData.from(repoDomain.extensions),
                                )
                            }
                        }

                        is ExtensionsUiState.Error -> {
                            showLoading(false)
                            binding.errorRoot.root.isVisible = true
                            val errorText = state.code?.let { "Error $it: ${state.message}" }
                                ?: state.message
                            binding.errorRoot.tvError.text = errorText
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region Show Loading
    private fun showLoading(toShow: Boolean) {
        Log.i(tag, "loader $toShow")
        binding.loading.isVisible = toShow

        if (isDialogShowing) {
            dialog?.let {
                Log.i(tag, "isDialogShowing $isDialogShowing -> loader $toShow")
                it.showLoaderOnButton(toShow)
                if (!toShow) {
                    it.dismiss()
                }
            }
        }
    }
    //endregion

    //region showInputDialog
    private fun addChipDialog() {
        Log.i(tag, "showInputDialog")
        if (isDialogShowing) return
        isDialogShowing = true

        dialog = InputDialogFragment(
            title = getString(R.string.add_repo),
            description = getString(R.string.add_repo_description),
            hint = getString(R.string.add_repo_hint),
            needCancelButton = false,
            onInputSubmitted = { dlg, url ->
                Log.i(tag, "url $url")

                if (!UrlUtils.isValidRepoUrl(url)) {
                    // invalid url -> show toast
                    Toast.makeText(requireContext(), "Invalid Repo URL", Toast.LENGTH_SHORT).show()
                } else if (animeRepos.any { it.repoUrl == url }) {
                    // Repo already added -> show toast
                    Toast.makeText(requireContext(), "This repo already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // valid -> save the rep
                    Log.i(tag, "add new repo")
                    extensionViewModel.addRepo(url)
                }
            },
            onDismissListener = {
                isDialogShowing = false
                dialog = null
            },
        )

        dialog?.show(parentFragmentManager, "input_dialog")
    }
    //endregion

    //region onChipClicked
    private fun onChipClicked(chip: Chip, position: Int) {

        if (chip.isSelected) return

        if (chip.isAddRepoChip) {
            addChipDialog()
        } else {
            Log.i(tag, "load extensions on chip clicked")
            repoUrlChipsAdapter.selectChip(chip, position)

            // Clear previous extensions
            lifecycleScope.launch {
                extensionAdapter.submitData(androidx.paging.PagingData.empty())
            }

            extensionViewModel.loadExtensions(chip.url)
        }
    }
    //endregion
}
