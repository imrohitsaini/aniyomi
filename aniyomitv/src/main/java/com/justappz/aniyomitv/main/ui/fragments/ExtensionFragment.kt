package com.justappz.aniyomitv.main.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseFragment
import com.justappz.aniyomitv.core.components.dialog.InputDialogFragment
import com.justappz.aniyomitv.databinding.FragmentExtensionBinding

class ExtensionFragment : BaseFragment() {

    //region variables
    private var _binding: FragmentExtensionBinding? = null
    private val binding get() = _binding!!
    private val tag = "ExtensionFragment"
    private var isDialogShowing = false
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
            onInputSubmitted = { input ->
                Log.i(tag, "url $input")
            },
            onDismissListener = {
                isDialogShowing = false
            },
        )

        dialog.show(parentFragmentManager, "input_dialog")
    }
    //endregion
}
