package com.justappz.aniyomitv.core.components.buttons

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.core.enums.ButtonType
import com.justappz.aniyomitv.databinding.PrimaryButtonComponentBinding

class PrimaryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.buttonStyle,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: PrimaryButtonComponentBinding =
        PrimaryButtonComponentBinding.inflate(LayoutInflater.from(context), this)

    var type: ButtonType = ButtonType.POSITIVE
        set(value) {
            field = value
            applyType()
        }

    private var savedText: CharSequence? = null


    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.PrimaryButton) {
                val typeValue = getInt(R.styleable.PrimaryButton_buttonType, 0)
                type = if (typeValue == 0) ButtonType.POSITIVE else ButtonType.NEGATIVE

                context.withStyledAttributes(it, intArrayOf(android.R.attr.text)) {
                    val text = getString(0)
                    text?.let { setText(text) }
                }
            }
        }

        applyType()
        isFocusable = true
        isFocusableInTouchMode = true

        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.focus_animator)

        showLoader(false)
    }

    private fun applyType() {
        setBackgroundResource(type.backgroundRes)
    }

    fun setText(text: String) {
        savedText = text
        binding.primaryButtonText.text = text
    }

    fun showLoader(show: Boolean) {
        if (show) {
            savedText = binding.primaryButtonText.text
            binding.primaryButtonText.text = ""
        } else {
            binding.primaryButtonText.text = savedText ?: ""
        }
        binding.primaryButtonLoader.isVisible = show
        binding.primaryButtonText.isVisible = !show
    }
}
