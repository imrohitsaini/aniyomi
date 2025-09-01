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
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.core.model.Options
import com.justappz.aniyomitv.core.model.RadioButtonDialogModel
import com.justappz.aniyomitv.databinding.ItemOptionsBinding
import com.justappz.aniyomitv.databinding.RadioButtonDialogBinding

class RadioButtonDialog(
    private val radioButtonDialogModel: RadioButtonDialogModel,
    private val onDone: (positon: Int) -> Unit,
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

        setupRadioGroup(radioButtonDialogModel, binding.root.context)

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
                ViewGroup.LayoutParams.MATCH_PARENT, // or MATCH_PARENT if fullscreen
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
        model: RadioButtonDialogModel,
        context: Context,
    ) {

        binding.rvOptions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Track last selected position
        var lastSelectedPosition = model.options.indexOfFirst { it.isSelected == true }


        var optionsAdapter: BaseRecyclerViewAdapter<Options, ItemOptionsBinding>? = null
        optionsAdapter = BaseRecyclerViewAdapter(
            items = model.options,
            bindingInflater = { inflater, parent, attach ->
                ItemOptionsBinding.inflate(inflater, parent, attach)
            },
        ) { option, position ->
            rbOption.text = option.buttonTitle

            // Always bind the current state correctly
            rbOption.isChecked = option.isSelected == true

            rbOption.setOnCheckedChangeListener(null) // avoid unwanted triggers while recycling
            rbOption.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Update model
                    if (lastSelectedPosition != -1 && lastSelectedPosition != position) {
                        model.options[lastSelectedPosition].isSelected = false
                        binding.rvOptions.post {
                            optionsAdapter?.notifyItemChanged(lastSelectedPosition)
                        }
                    }

                    model.options[position].isSelected = true
                    lastSelectedPosition = position
                }
            }
        }
        binding.rvOptions.adapter = optionsAdapter

        binding.btnDone.setOnClickListener {
            val selectedOption = model.options.indexOfFirst { it.isSelected == true }
            if (selectedOption != -1) {
                onDone(selectedOption)
                dismiss()
            } else {
                Toast.makeText(context, "No option selected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
