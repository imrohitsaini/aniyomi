package com.justappz.aniyomitv.core.components.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.databinding.DialogExitBinding

class ExitDialogFragment(
    private val title: String = "Are you sure want to exit?",
    private val onYes: (() -> Unit),
    private val onDismissListener: (() -> Unit)? = null,
) : DialogFragment() {

    //region variables
    private var _binding: DialogExitBinding? = null
    private val binding get() = _binding!!
    private val tag = "ExitDialogFragment"
    //endregion

    //region onCreateDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.i(tag, "onCreateDialog")
        val dialog = super.onCreateDialog(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppDialog_Fullscreen)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // remove default title
        return dialog
    }
    //endregion

    //region onCreateView
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.i(tag, "onCreateView")
        _binding = DialogExitBinding.inflate(inflater, container, false)

        binding.tvTitle.text = title


        binding.btnPositive.setOnClickListener {
            onYes()
            dismiss()
        }


        binding.btnNegative.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
    //endregion


    //region onStart
    override fun onStart() {
        super.onStart()
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, // or MATCH_PARENT if fullscreen
            )

            // Then adjust params to give margins
            val params = attributes
            val margin = resources.getDimensionPixelSize(R.dimen._60dp) // e.g. 16dp
            params.width = resources.displayMetrics.widthPixels - (margin * 2) // reduce width
            params.gravity = Gravity.CENTER // keep it centered
            attributes = params
        }
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
}
