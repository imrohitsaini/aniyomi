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
        setStyle(STYLE_NORMAL, R.style.AppDialog_Fullscreen)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)      // 🚫 User cannot cancel
        dialog.setCanceledOnTouchOutside(false) // 🚫 No cancel on outside touch
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
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            val params = attributes
            val margin = resources.getDimensionPixelSize(R.dimen._60dp)
            params.width = resources.displayMetrics.widthPixels - (margin * 2)
            params.gravity = Gravity.CENTER
            attributes = params
        }
        isRunning = true
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
