package com.justappz.aniyomitv.extensions_management.dialog

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
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.core.util.FileUtils
import com.justappz.aniyomitv.databinding.ExtensionDialogBinding
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain
import kotlinx.coroutines.launch

class ExtensionDialogFragment(
    private val extension: ExtensionDomain,
    private val onDismissListener: (() -> Unit)? = null,
) : DialogFragment() {

    //region variables
    private var _binding: ExtensionDialogBinding? = null
    private val binding get() = _binding!!
    private val tag = "ExtensionDialogFragment"
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
        _binding = ExtensionDialogBinding.inflate(inflater, container, false)

        binding.dialogTitle.text = extension.name.replace("Aniyomi: ", "")
        binding.dialogDescription.text = "${extension.version}-${extension.code}"

        // If installed btn ok text to -> re install
        // If update available btn ok text to -> update

        binding.btnOk.setOnClickListener {

            // Download and install the extension here
            lifecycleScope.launch {
                extension.fileUrl?.let {
                    showLoaderOnButton(true)
                    val file = FileUtils.downloadFile(
                        context = requireContext(),
                        fileUrl = it,
                    )

                    if (file != null) {
                        showLoaderOnButton(false)
                        Log.d(tag, "File - ${file.path}")
                        // File downloaded, now install / process it
                        // e.g., start installation intent or update UI
                        // send to update the list
                    } else {
                        // Failed to download
                        // Toast the error
                    }
                }
            }

            // Send the function to update the extensions view

        }

        binding.btnUninstall.setOnClickListener {
            // uninstall the extension here

            dismiss()
        }

        // If installed btn uninstall is visible
        binding.btnUninstall.isVisible = extension.isInstalled

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

    //region showLoaderOnButton
    fun showLoaderOnButton(show: Boolean) {
        binding.btnOk.showLoader(show)
    }
    //endregion
}
