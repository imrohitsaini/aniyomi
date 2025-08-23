package com.justappz.aniyomitv.core.components.chips

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.core.enums.ChipType
import com.justappz.aniyomitv.databinding.ComponentChipBinding

class ChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentChipBinding =
        ComponentChipBinding.inflate(LayoutInflater.from(context), this, true)

    var type: ChipType = ChipType.FILTER
        set(value) {
            field = value
//            background = ContextCompat.getDrawable(context, value.backgroundRes)
        }

    // Zoom scale properties
    private val focusedScale = 1.12f
    private val normalScale = 1.0f
    private val animationDuration = 150L

    init {
        // Focusable for TV navigation
        isFocusable = true
        isFocusableInTouchMode = true
        clipChildren = false
        clipToPadding = false

        // Apply attributes from XML
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.ChipView) {
                val text = getString(R.styleable.ChipView_chipText)
                val iconResId = getResourceId(R.styleable.ChipView_chipIcon, 0)
                val typeValue = getInt(R.styleable.ChipView_chipType, 0)
                val chipSelected = getBoolean(R.styleable.ChipView_chipSelected, false)

                type = if (typeValue == 0) ChipType.FILTER else ChipType.FILTER
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
        if (!text.isNullOrEmpty()) {
            binding.tvChipName.text = text
            binding.tvChipName.isVisible = true
        } else {
            binding.tvChipName.isVisible = false
        }
    }

    fun setChipIcon(res: Int?) {
        res?.let {
            try {
                binding.ivChipEnd.setImageResource(it)
                binding.ivChipEnd.isVisible = true
            } catch (e: Exception) {
                binding.ivChipEnd.isVisible = false
                Log.e("ChipView", "Error setting icon: ${e.message}")
            }
        } ?: run {
            binding.ivChipEnd.isVisible = false
        }
    }

    fun getText(): String = binding.tvChipName.text.toString()

    fun setSelectedState(isSelected: Boolean) {
        this.isSelected = isSelected // applied on FrameLayout root
        binding.root.refreshDrawableState() // ensure selector updates
    }
}
