package com.justappz.aniyomitv.extensions.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.constants.PrefsKeys
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.dialog.InputDialogFragment
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.core.error.ErrorHandler
import com.justappz.aniyomitv.core.util.PrefsManager
import com.justappz.aniyomitv.core.util.UrlUtils
import com.justappz.aniyomitv.core.util.toJsonArray
import com.justappz.aniyomitv.databinding.FragmentExtensionBinding
import com.justappz.aniyomitv.extensions.dialog.ExtensionDialogFragment
import com.justappz.aniyomitv.extensions.domain.model.Chip
import com.justappz.aniyomitv.extensions.domain.model.ExtensionDomain
import com.justappz.aniyomitv.extensions.domain.model.ExtensionRepositoriesDetailsDomain
import com.justappz.aniyomitv.extensions.domain.usecase.GetExtensionRepoDetailsUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.InsertExtensionRepoUrlUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.ObserveExtensionsUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.RefreshExtensionsUseCase
import com.justappz.aniyomitv.extensions.presentation.adapters.ExtensionPagingAdapter
import com.justappz.aniyomitv.extensions.presentation.adapters.RepoChipsAdapter
import com.justappz.aniyomitv.extensions.presentation.viewmodel.ExtensionViewModel
import com.justappz.aniyomitv.extensions.utils.ExtensionUtils
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ExtensionFragment : BaseFragment() {

    //region variables
    private var _binding: FragmentExtensionBinding? = null
    private val binding get() = _binding!!
    private val tag = "ExtensionFragment"
    private var isNsfwEnabled = false
    private val viewModel: ExtensionViewModel by viewModels {
        ViewModelFactory {
            ExtensionViewModel(
                Injekt.get<GetExtensionRepoDetailsUseCase>(),
                Injekt.get<InsertExtensionRepoUrlUseCase>(),
                Injekt.get<ObserveExtensionsUseCase>(),
                Injekt.get<RefreshExtensionsUseCase>(),
            )
        }
    }

    // Need this to not add duplicate repo url
    private var animeRepos: List<ExtensionRepositoriesDetailsDomain> = arrayListOf()
    private var chips: MutableList<Chip> = arrayListOf()
    private var selectedChip: Chip? = null
    private lateinit var repoUrlChipsAdapter: RepoChipsAdapter
    private var addRepoDialog: InputDialogFragment? = null
    private var extensionDialog: ExtensionDialogFragment? = null
    private val extensionAdapter = ExtensionPagingAdapter()
    private var isNewRepo = false // set this to true when new repo is added
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

        isNsfwEnabled = PrefsManager.getBoolean(PrefsKeys.PREFERRED_NSFW_SOURCE, false)

        Log.d(tag, "init")
        reposAdapterProperties()
        extensionAdapterProperties()
        observeRepo()
        observeExtensionState()
        viewModel.loadRepoUrls()
    }
    //endregion

    //region reposAdapterProperties()
    private fun reposAdapterProperties() {
        Log.d(tag, "reposAdapterProperties")
        repoUrlChipsAdapter = RepoChipsAdapter(emptyList()).apply {
            onItemClick = { chip, position -> onChipClicked(chip, position) }
        }
        binding.rvRepos.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
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
    /**
     * Success will be collected twice on happy flow
     * 1. When user has stored the urls in the db before this instance
     * 2. When the user has added the new url. For new url case we do not need to load extensions again as extensions will
     * be loaded first then added to the db for accurate data
     * */
    private fun observeRepo() {
        Log.d(tag, "observeRepo")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.repoUrls.collect { state ->
                    when (state) {
                        is BaseUiState.Error -> {
                            Log.d(tag, "RepoUiState.Error")
                            showLoading(false)
                            ErrorHandler.show(
                                ctx,
                                state.error,
                                binding.errorRoot.tvError,
                            )
                            binding.errorRoot.root.isVisible = true
                            binding.rvExtensions.isVisible = false
                        }

                        BaseUiState.Idle -> {
                            Log.d(tag, "RepoUiState.Idle")
                        }

                        BaseUiState.Loading -> {
                            Log.d(tag, "RepoUiState.Loading")
                            showLoading(true)
                        }

                        is BaseUiState.Success -> {
                            Log.d(tag, "RepoUiState.Success")
                            showLoading(false)
                            animeRepos = state.data
                            Log.i(tag, "repoUrls fetched ${animeRepos.toJsonArray()}")

                            binding.errorRoot.root.isVisible = false
                            binding.rvExtensions.isVisible = true

                            if (isNewRepo) {
                                addRepoDialog?.let {
                                    Log.i(tag, "disable loader on button for new repo")
                                    it.showLoaderOnButton(false)
                                    it.dismiss()
                                }
                            }

                            updateRepoChips(animeRepos)
                            viewModel.resetRepoState()
                        }

                        BaseUiState.Empty -> {
                            showLoading(false)
                            Log.d(tag, "Empty repo urls")

                            // Show the error
                            ErrorHandler.show(
                                ctx,
                                AppError.UnknownError(
                                    message = "No repos added",
                                    displayType = ErrorDisplayType.INLINE,
                                ),
                                binding.errorRoot.tvError,
                            )
                            binding.errorRoot.root.isVisible = true
                            binding.rvExtensions.isVisible = false

                            // Update the chip to add the new item
                            updateRepoChips(emptyList())
                        }
                    }

                }
            }
        }
    }
    //endregion

    //region updateRepoChips
    private fun updateRepoChips(repositoriesDetailsDomains: List<ExtensionRepositoriesDetailsDomain>) {
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
        repoUrlChipsAdapter.forceUpdateList(chips)


        selectedChip?.let {
            // Do not load the extensions for the new repo
            if (isNewRepo) {
                isNewRepo = false
            } else {
                viewModel.loadExtensions(it.url)
            }
        }
    }
    //endregion

    //region observeExtensionState
    private fun observeExtensionState() {
        Log.d(tag, "observeExtensionState")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.extensionState.collect { state ->
                    when (state) {
                        BaseUiState.Idle -> {
                            Log.d(tag, "ExtensionsUiState.Idle")
                        }

                        is BaseUiState.Loading -> {
                            Log.d(tag, "ExtensionsUiState.Loading")
                            showLoading(true)
                            binding.errorRoot.root.isVisible = false
                        }

                        is BaseUiState.Success -> {
                            Log.d(tag, "ExtensionsUiState.Success")
                            val repoDomain = state.data
                            Log.i(
                                tag,
                                "extensions successfully fetched ${repoDomain.extensions.size} for url ${repoDomain.repoUrl}",
                            )
                            // Submit list to Paging adapter
                            lifecycleScope.launch {
                                val updatedList = repoDomain.extensions.map { extension ->
                                    val installedInfo = ExtensionUtils.getInstalledExtensionByPackageName(
                                        context = ctx,
                                        packageName = extension.pkg,
                                    )

                                    extension.copy(
                                        installedExtensionInfo = installedInfo,
                                    )
                                }

                                // Sort: installed first, then not installed
                                var sortedList =
                                    updatedList.sortedByDescending { it.installedExtensionInfo?.installed == true }

                                // Filter nsfw if not enabled
                                if (!isNsfwEnabled) {
                                    sortedList = sortedList.filter { it.nsfw == 0 }
                                }

                                // Submit updated + sorted list
                                extensionAdapter.submitData(
                                    pagingData = PagingData.from(sortedList),
                                )
                            }

                            binding.errorRoot.root.isVisible = false
                            binding.rvExtensions.isVisible = true

                            viewModel.resetExtensionState()

                            if (isNewRepo) {
                                viewModel.addRepo(repoDomain.repoUrl)
                            } else {
                                showLoading(false)
                            }
                        }

                        is BaseUiState.Error -> {
                            Log.d(tag, "ExtensionsUiState.Error ")
                            showLoading(false)

                            // If is new repo error will be toast
                            val error = if (isNewRepo) {
                                AppError.UnknownError(
                                    message = state.error.message,
                                    displayType = ErrorDisplayType.TOAST,
                                )
                            } else {
                                binding.errorRoot.root.isVisible = true
                                binding.rvExtensions.isVisible = false
                                state.error
                            }

                            ErrorHandler.show(ctx, error, binding.errorRoot.tvError)
                        }

                        BaseUiState.Empty -> {
                            showLoading(false)
                            Log.d(tag, "No valid extensions detected")
                            // If is new repo error will be toast
                            val message = "No valid extensions detected"

                            // While adding a repo show error on toast
                            val error = if (isNewRepo) {
                                AppError.UnknownError(
                                    message = message,
                                    displayType = ErrorDisplayType.TOAST,
                                )
                            } else {
                                // For existing repo, show error inline
                                binding.errorRoot.root.isVisible = true
                                binding.rvExtensions.isVisible = false
                                AppError.UnknownError(
                                    message = message,
                                    displayType = ErrorDisplayType.INLINE,
                                )
                            }
                            ErrorHandler.show(ctx, error, binding.errorRoot.tvError)
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

    //region addNewRepoDialog
    private fun addNewRepoDialog() {
        Log.i(tag, "addChipDialog")

        addRepoDialog = InputDialogFragment(
            title = getString(R.string.add_repo),
            description = getString(R.string.add_repo_description),
            hint = getString(R.string.add_repo_hint),
            needCancelButton = false,
            onInputSubmitted = { dlg, url ->
                Log.i(tag, "url $url")

                if (!UrlUtils.isValidRepoUrl(url)) {
                    // invalid url -> show toast
                    ErrorHandler.show(
                        context = ctx,
                        error = AppError.ValidationError(
                            message = "Invalid repo url",
                        ),
                    )
                } else if (animeRepos.any { it.repoUrl == url }) {
                    // Repo already added -> show toast
                    ErrorHandler.show(
                        context = ctx,
                        error = AppError.ValidationError(
                            message = "This repo already exists",
                        ),
                    )
                } else {
                    // valid -> load the extensions -> then save it to new repo after success
                    Log.i(tag, "load the extensions for new url")
                    isNewRepo = true
                    addRepoDialog?.showLoaderOnButton(true)
                    viewModel.loadExtensionsFromNewUrl(url)
                }
            },
            onDismissListener = {
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
            addNewRepoDialog()
        } else {
            Log.i(tag, "load extensions on chip clicked")
            repoUrlChipsAdapter.selectChip(chip, position)

            viewModel.loadExtensions(chip.url)
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
                viewModel.loadExtensions(repoUrl)
            },
            onDismissListener = {

            },
        )

        extensionDialog?.show(parentFragmentManager, "extension_dialog")
    }
    //endregion
}
