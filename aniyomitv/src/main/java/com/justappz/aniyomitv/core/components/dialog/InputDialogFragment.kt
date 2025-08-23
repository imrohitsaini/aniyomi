package com.justappz.aniyomitv.core.components.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.core.util.FocusKeyHandler
import com.justappz.aniyomitv.databinding.InputDialogBinding

class InputDialogFragment(
    private val title: String,
    private val description: String,
    private val hint: String,
    private val onInputSubmitted: (InputDialogFragment, String) -> Unit,
    private val onDismissListener: (() -> Unit)? = null,
    private val needCancelButton: Boolean = false,
) : DialogFragment() {

    //region variables
    private var _binding: InputDialogBinding? = null
    private val binding get() = _binding!!
    private val tag = "InputDialogFragment"
    //endregion

    //region onCreateDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.i(tag, "onCreateDialog")
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // remove default title
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
        return dialog
    }
    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.i(tag, "onCreateView")
        _binding = InputDialogBinding.inflate(inflater, container, false)

        binding.dialogTitle.text = title
        binding.dialogDescription.text = description
        binding.etInput.hint = hint

        binding.btnOk.setOnClickListener {
            val input = binding.etInput.text.toString().trim()
            onInputSubmitted(this, input)
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.etInput.setOnKeyListener(
            FocusKeyHandler(
                onDown = {
                    binding.btnOk.requestFocus()
                },
            ),
        )

        binding.btnCancel.isVisible = needCancelButton

        return binding.root
    }
    //endregion

    //region onDestroyView
    override fun onDestroyView() {
        Log.i(tag, "onDestroyView")
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region onDismiss
    override fun onDismiss(dialog: DialogInterface) {
        Log.i(tag, "onDismiss")
        super.onDismiss(dialog)
        onDismissListener?.invoke() // call the listener
    }
    //endregion

    //region showLoaderOnButton
    fun showLoaderOnButton(show: Boolean) {
        binding.btnOk.showLoader(show)
    }
    //endregion
}
