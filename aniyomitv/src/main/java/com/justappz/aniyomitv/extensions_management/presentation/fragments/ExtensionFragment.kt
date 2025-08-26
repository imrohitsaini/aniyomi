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
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.dialog.InputDialogFragment
import com.justappz.aniyomitv.core.util.UrlUtils
import com.justappz.aniyomitv.core.util.toJsonArray
import com.justappz.aniyomitv.databinding.FragmentExtensionBinding
import com.justappz.aniyomitv.extensions_management.dialog.ExtensionDialogFragment
import com.justappz.aniyomitv.extensions_management.domain.model.AnimeRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions_management.domain.model.Chip
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetRepoUrlsUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.RemoveRepoUrlUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.SaveRepoUrlUseCase
import com.justappz.aniyomitv.extensions_management.presentation.adapters.ExtensionPagingAdapter
import com.justappz.aniyomitv.extensions_management.presentation.adapters.RepoChipsAdapter
import com.justappz.aniyomitv.extensions_management.presentation.states.ExtensionsUiState
import com.justappz.aniyomitv.extensions_management.presentation.states.RepoUiState
import com.justappz.aniyomitv.extensions_management.presentation.viewmodel.ExtensionViewModel
import com.justappz.aniyomitv.extensions_management.utils.ExtensionUtils
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
    private var addRepoDialog: InputDialogFragment? = null
    private var extensionDialog: ExtensionDialogFragment? = null
    private val extensionAdapter = ExtensionPagingAdapter()

    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d(tag, "onCreateView")
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_extension, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
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

    //region init()
    private fun init() {
        Log.d(tag, "init")
        reposAdapterProperties()
        extensionAdapterProperties()
        observeRepo()
        observeExtensionState()
        extensionViewModel.loadRepoUrls()
    }
    //endregion

    //region reposAdapterProperties()
    private fun reposAdapterProperties() {
        Log.d(tag, "reposAdapterProperties")
        repoUrlChipsAdapter = RepoChipsAdapter(emptyList()).apply {
            onItemClick = { chip, position -> onChipClicked(chip, position) }
        }
        binding.rvRepos.layoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        binding.rvRepos.adapter = repoUrlChipsAdapter
    }
    //endregion

    //region extensionAdapterProperties
    private fun extensionAdapterProperties() {
        Log.d(tag, "extensionAdapterProperties")
        // Extension RecyclerView setup
        extensionAdapter.onItemClick = { item, _ -> showExtensionDialog(item) }
        binding.rvExtensions.layoutManager = GridLayoutManager(ctx, 5)
        binding.rvExtensions.adapter = extensionAdapter
        extensionAdapter.attachRecyclerView(binding.rvExtensions)
    }
    //endregion

    //region observeRepo
    private fun observeRepo() {
        Log.d(tag, "observeRepo")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                extensionViewModel.repoUrls.collect { reposState ->
                    when (reposState) {
                        is RepoUiState.Error -> {
                            Log.d(tag, "RepoUiState.Error")
                            showLoading(false)
                        }

                        RepoUiState.Idle -> {
                            Log.d(tag, "RepoUiState.Idle")
                            showLoading(false)
                        }

                        RepoUiState.Loading -> {
                            Log.d(tag, "RepoUiState.Loading")
                            showLoading(true)
                        }

                        is RepoUiState.Success -> {
                            Log.d(tag, "RepoUiState.Success")
                            showLoading(false)
                            animeRepos = reposState.data
                            Log.i(tag, "repoUrls fetched ${animeRepos.toJsonArray()}")

                            updateRepoChips(animeRepos)
                            extensionViewModel.resetRepoState()
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
        Log.d(tag, "observeExtensionState")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                extensionViewModel.extensionState.collect { state ->
                    when (state) {
                        ExtensionsUiState.Idle -> {
                            Log.d(tag, "ExtensionsUiState.Idle")
                            showLoading(false)
                        }

                        is ExtensionsUiState.Loading -> {
                            Log.d(tag, "ExtensionsUiState.Loading ")
                            showLoading(true)
                            binding.errorRoot.root.isVisible = false
                        }

                        is ExtensionsUiState.Success -> {
                            Log.d(tag, "ExtensionsUiState.Success")
                            showLoading(false)
                            val repoDomain = state.data
                            Log.i(
                                tag,
                                "extensions successfully fetched ${repoDomain.extensions.size} for url ${repoDomain.repoUrl}",
                            )
                            // Submit list to Paging adapter
                            lifecycleScope.launch {
                                val updatedList = repoDomain.extensions.map { extension ->
                                    val installedInfo = ExtensionUtils.getInstalledExtensionByPackageName(
                                        context = requireContext(),
                                        packageName = extension.pkg
                                    )

                                    extension.copy(
                                        installedExtensionInfo = installedInfo
                                    )
                                }

                                // Sort: installed first, then not installed
                                val sortedList = updatedList.sortedByDescending { it.installedExtensionInfo?.installed == true }

                                // Clear previous extensions
                                extensionAdapter.submitData(androidx.paging.PagingData.empty())

                                // Submit updated + sorted list
                                extensionAdapter.submitData(
                                    pagingData = androidx.paging.PagingData.from(sortedList),
                                )
                            }
                        }

                        is ExtensionsUiState.Error -> {
                            Log.d(tag, "ExtensionsUiState.Error ")
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
        binding.loading.isVisible = toShow

        if (isDialogShowing) {
            addRepoDialog?.let {
                Log.i(tag, "isDialogShowing ${true} -> loader $toShow")
                it.showLoaderOnButton(toShow)
                if (!toShow) {
                    it.dismiss()
                }
            }
            isDialogShowing = false
        }
    }
    //endregion

    //region addChipDialog
    private fun addChipDialog() {
        Log.i(tag, "addChipDialog")
        if (isDialogShowing) return
        isDialogShowing = true

        addRepoDialog = InputDialogFragment(
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
                addRepoDialog = null
            },
        )

        addRepoDialog?.show(parentFragmentManager, "input_dialog")
    }
    //endregion

    //region onChipClicked
    private fun onChipClicked(chip: Chip, position: Int) {
        Log.d(tag, "onChipClicked")

        if (chip.isSelected) return

        if (chip.isAddRepoChip) {
            addChipDialog()
        } else {
            Log.i(tag, "load extensions on chip clicked")
            repoUrlChipsAdapter.selectChip(chip, position)

            extensionViewModel.loadExtensions(chip.url)
        }
    }
    //endregion

    //region extensionDialog
    private fun showExtensionDialog(extensionDomain: ExtensionDomain) {
        Log.i(tag, "showExtensionDialog")

        extensionDialog = ExtensionDialogFragment(
            extension = extensionDomain,
            onRefreshed = { repoUrl ->
                Log.d(tag, "onRefreshed $repoUrl")
            },
            onDismissListener = {

            },
        )

        extensionDialog?.show(parentFragmentManager, "extension_dialog")
    }
    //endregion
}
