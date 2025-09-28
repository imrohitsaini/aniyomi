package com.justappz.aniyomitv.search.presentation.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseActivity
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.constants.PrefsKeys
import com.justappz.aniyomitv.core.ViewModelFactory
import com.justappz.aniyomitv.core.components.dialog.RadioButtonDialog
import com.justappz.aniyomitv.core.error.ErrorHandler
import com.justappz.aniyomitv.core.model.Options
import com.justappz.aniyomitv.core.model.RadioButtonDialogModel
import com.justappz.aniyomitv.core.util.FocusKeyHandler
import com.justappz.aniyomitv.core.util.PrefsManager
import com.justappz.aniyomitv.databinding.ActivityAnimeSearchBinding
import com.justappz.aniyomitv.discover.domain.model.InstalledExtensions
import com.justappz.aniyomitv.discover.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.search.presentation.viewmodel.SearchViewModel
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class AnimeSearchActivity : BaseActivity(), View.OnClickListener {

    //region variables
    private lateinit var binding: ActivityAnimeSearchBinding
    private val tag = "EpisodesActivity"
    private var installedExtensions: MutableList<InstalledExtensions> = arrayListOf()
    private var selectedAnimeSource: AnimeHttpSource? = null

    private val viewModel: SearchViewModel by viewModels {
        ViewModelFactory {
            SearchViewModel(
                Injekt.get<GetInstalledExtensionsUseCase>(),
            )
        }
    }
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(tag, "onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_anime_search)

        init()

    }
    //endregion

    //region init
    private fun init() {
        Log.d(tag, "init")
        observeInstalledExtensions()
        viewModel.getExtensions(ctx)

        binding.etInput.setOnKeyListener(
            FocusKeyHandler(
                onRight = {
                    binding.tvChangeSource.requestFocus()
                },
            ),
        )
        binding.tvChangeSource.setOnKeyListener(
            FocusKeyHandler(
                onLeft = {
                    binding.etInput.requestFocus()
                },
            ),
        )
        binding.tvChangeSource.setOnClickListener(this)

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


                            binding.tvChangeSource.isVisible = installedExtensions.size >= 2

                            binding.tvChangeSource.text = getString(
                                R.string.change_source,
                                installedExtensions[selectionIndex].appName,
                            )

                            viewModel.resetExtensionState()
                        }

                        BaseUiState.Empty -> {
                            showLoading(false)

                            // Show error in line
                            binding.errorRoot.tvError.text = "No extensions detected"
                            binding.errorRoot.root.isVisible = true
                            binding.tvChangeSource.isVisible = false
                        }
                    }

                }
            }
        }
    }
    //endregion

    //region onClick
    override fun onClick(view: View?) {
        Log.d(tag, "onClick")
        view?.let {
            when (view) {
                binding.tvChangeSource -> {
                    changeSourceDialog()
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

                binding.tvChangeSource.text = getString(
                    R.string.change_source,
                    selected?.appName ?: "",
                )

                // Save this as preference for next time
                selected?.let {
                    PrefsManager.putString(PrefsKeys.PREFERRED_EXTENSION, it.appName)
                }
            },
            onDismissListener = {},
        )
        dialog.show(supportFragmentManager, "options_dialog")
    }
    //endregion
}
