package com.justappz.aniyomitv.main.ui.fragments

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
import com.justappz.aniyomitv.databinding.FragmentExtensionBinding
import com.justappz.aniyomitv.extensions_management.domain.states.ExtensionsUiState
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
        ViewModelFactory { ExtensionViewModel(Injekt.get()) }
    }
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
//        observeExtensionState()
    }
    //endregion

    //region observeExtensionState
    private fun observeExtensionState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                extensionViewModel.extensionState.collect { state ->
                    when (state) {
                        is ExtensionsUiState.Loading -> {
                            binding.loading.isVisible = true
                            binding.errorRoot.root.isVisible = false
                        }

                        is ExtensionsUiState.Success -> {
                            binding.loading.isVisible = false
                            binding.errorRoot.root.isVisible = false
                        }

                        is ExtensionsUiState.Error -> {
                            binding.loading.isVisible = false
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
            onInputSubmitted = { dlg, input ->
                Log.i(tag, "url $input")
                Log.i(tag, "url $input")
                if (ValidationUtils.isValidRepoUrl(input)) {
                    // valid -> dismiss dialog
                    dlg.dismiss()
                } else {
                    // invalid -> show toast
                    Toast.makeText(requireContext(), "Invalid Repo URL", Toast.LENGTH_SHORT).show()
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
