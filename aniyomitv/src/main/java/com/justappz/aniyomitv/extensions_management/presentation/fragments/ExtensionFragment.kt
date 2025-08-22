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
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.dialog.InputDialogFragment
import com.justappz.aniyomitv.core.util.ValidationUtils
import com.justappz.aniyomitv.core.util.toJsonArray
import com.justappz.aniyomitv.databinding.FragmentExtensionBinding
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetRepoUrlsUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.RemoveRepoUrlUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.SaveRepoUrlUseCase
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
    private lateinit var repoUrls: List<String>
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
        binding.addRepoRoot.chipRepo.setOnClickListener {
            showInputDialog()
        }
        observeRepo()
        observeExtensionState()
        extensionViewModel.loadRepoUrls()
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
                            repoUrls = reposState.data
                            Log.i(tag, "repoUrls fetched ${repoUrls.toJsonArray()}")

                            //todo set recycler view of urls and extensions
                        }
                    }

                }
            }
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
                            Log.i(tag, "extensions successfully fetched ${repoDomain.extensions.size}")

                            // if repoUrls doesnt contain this new url, then add this url
                            if (!repoUrls.contains(repoDomain.repoUrl)) {
                                Log.i(tag, "extensions success, save the url & refresh")
                                extensionViewModel.addRepo(repoDomain.repoUrl)
                            } else {
                                // update the extensions list
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

    private fun showLoading(toShow: Boolean) {
        Log.i(tag, "loader $toShow")
        binding.loading.isVisible = toShow
    }
    //endregion

    //region showInputDialog
    private fun showInputDialog() {
        Log.i(tag, "showInputDialog")
        if (isDialogShowing) return
        isDialogShowing = true

        val dialog = InputDialogFragment(
            title = getString(R.string.add_repo),
            description = getString(R.string.add_repo_description),
            hint = getString(R.string.add_repo_hint),
            needCancelButton = false,
            onInputSubmitted = { dlg, url ->
                Log.i(tag, "url $url")
                Log.i(tag, "url $url")

                if (!ValidationUtils.isValidRepoUrl(url)) {
                    // invalid url -> show toast
                    Toast.makeText(requireContext(), "Invalid Repo URL", Toast.LENGTH_SHORT).show()
                } else if (repoUrls.contains(url)) {
                    // Repo already added -> show toast
                    Toast.makeText(requireContext(), "This repo already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // valid -> dismiss the dialog
                    dlg.dismiss()
                    extensionViewModel.loadExtensions(url)
                }
            },
            onDismissListener = {
                isDialogShowing = false
            },
        )

        dialog.show(parentFragmentManager, "input_dialog")
    }
    //endregion
}
