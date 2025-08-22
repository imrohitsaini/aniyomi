package com.justappz.aniyomitv.core.components.chips

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.core.enums.ChipType
import com.justappz.aniyomitv.databinding.ComponentChipBinding

class ChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentChipBinding = ComponentChipBinding.inflate(LayoutInflater.from(context), this, true)

    var type: ChipType = ChipType.CHIP_BUTTON
        set(value) {
            field = value
            binding.root.setBackgroundResource(value.backgroundRes)
        }

    // Zoom scale properties
    private val focusedScale = 1.1f
    private val normalScale = 1.0f
    private val animationDuration = 150L

    init {

        isFocusable = true
        isFocusableInTouchMode = true

        attrs?.let {
            context.withStyledAttributes(it, R.styleable.ChipView) {
                val text = getString(R.styleable.ChipView_chipText)
                val iconResId = getResourceId(R.styleable.ChipView_chipIcon, 0)
                val typeValue = getInt(R.styleable.ChipView_chipType, 0)
                val chipSelected = getBoolean(R.styleable.ChipView_chipSelected, false)
                type = if (typeValue == 0) ChipType.CHIP_BUTTON else ChipType.FILTER
                setText(text)
                setChipIcon(if (iconResId != 0) iconResId else null)
                setSelectedState(chipSelected)
            }
        }

        // Focus listener for zoom
        setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate()
                    .scaleX(focusedScale)
                    .scaleY(focusedScale)
                    .setDuration(animationDuration)
                    .start()
            } else {
                view.animate()
                    .scaleX(normalScale)
                    .scaleY(normalScale)
                    .setDuration(animationDuration)
                    .start()
            }
        }
    }

    fun setText(text: String?) {
        text?.let { binding.tvChipName.text = it }
    }

    fun setChipIcon(res: Int?) {
        res?.let {
            try {
                binding.ivChipEnd.setImageResource(it)
                binding.ivChipEnd.isVisible = true
            } catch (e: Exception) {
                binding.ivChipEnd.isVisible = false
                Log.e("ChipView", "e.message ${e.message}")
            }
        } ?: {
            binding.ivChipEnd.isVisible = false
        }
    }

    fun getText(): String = binding.tvChipName.text.toString()


    fun setSelectedState(isSelected: Boolean) {
        binding.root.isSelected = isSelected
    }


}
