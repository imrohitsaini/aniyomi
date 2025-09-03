package com.justappz.aniyomitv.core.components.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.databinding.DialogLoaderBinding

class LoaderDialog(
    private val title: String,
) : DialogFragment() {

    //region variables
    private var _binding: DialogLoaderBinding? = null
    private val binding get() = _binding!!
    private val tagName = "LoaderDialog"
    var isRunning = false
    //endregion

    //region onCreateDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.i(tagName, "onCreateDialog")
        val dialog = super.onCreateDialog(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppDialog_Normal)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
    //endregion

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.i(tagName, "onCreateView")
        _binding = DialogLoaderBinding.inflate(inflater, container, false)
        binding.tvTitle.text = title
        return binding.root
    }
    //endregion

    //region onStart
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        }
        isRunning = true

        // Consume BACK press explicitly
        requireDialog().setOnKeyListener { _, keyCode, event ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK &&
                event.action == android.view.KeyEvent.ACTION_UP
            ) {
                // Block back press
                true
            } else {
                false
            }
        }
    }
    //endregion

    //region onDestroyView
    override fun onDestroyView() {
        Log.i(tagName, "onDestroyView")
        super.onDestroyView()
        _binding = null
        isRunning = false
    }
    //endregion
}
