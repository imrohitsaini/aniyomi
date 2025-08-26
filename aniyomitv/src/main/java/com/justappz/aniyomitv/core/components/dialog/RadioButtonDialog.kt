package com.justappz.aniyomitv.core.components.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.core.model.Options
import com.justappz.aniyomitv.core.model.RadioButtonDialogModel
import com.justappz.aniyomitv.databinding.RadioButtonDialogBinding

class RadioButtonDialog(
    private val radioButtonDialogModel: RadioButtonDialogModel,
    private val onDone: (option: Options) -> Unit,
    private val onDismissListener: (() -> Unit)? = null,
) : DialogFragment() {

    //region variables
    private var _binding: RadioButtonDialogBinding? = null
    private val binding get() = _binding!!
    private val tag = "RadioButtonDialog"
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
        _binding = RadioButtonDialogBinding.inflate(inflater, container, false)

        binding.tvOptionTitle.text = radioButtonDialogModel.title
        binding.tvOptionDescription.text = radioButtonDialogModel.description

        setupRadioGroup(binding.radioGroupOption, radioButtonDialogModel, binding.root.context)

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

    fun setupRadioGroup(
        radioGroup: RadioGroup,
        model: RadioButtonDialogModel,
        context: Context,
    ) {
        radioGroup.removeAllViews() // clear old items if any

        model.options.forEachIndexed { index, option ->
            val radioButton = RadioButton(context).apply {
                id = View.generateViewId()
                text = option.buttonTitle
                isChecked = model.isDefaultSelected == true && option.isSelected == true// select first if default
            }
            radioGroup.addView(radioButton)
        }

        binding.btnDone.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton = radioGroup.findViewById<RadioButton>(selectedId)
                val selectedText = selectedRadioButton.text.toString()

                // Search in model.options by text
                val selectedOption = model.options.firstOrNull { it.buttonTitle == selectedText }
                selectedOption?.let {
                    onDone(it)
                    dismiss() // close dialog after done
                }
            }
        }
    }
}
