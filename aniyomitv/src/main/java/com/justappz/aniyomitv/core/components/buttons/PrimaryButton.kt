package com.justappz.aniyomitv.core.components.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.core.enums.ButtonType

@SuppressLint("AppCompatCustomView")
class PrimaryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.buttonStyle,
) : Button(context, attrs, defStyleAttr) {

    var type: ButtonType = ButtonType.POSITIVE
        set(value) {
            field = value
            applyType()
        }

    // Zoom scale
    private val focusedScale = 1.1f
    private val normalScale = 1.0f
    private val animationDuration = 150L

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.PrimaryButton) {
                val typeValue = getInt(R.styleable.PrimaryButton_buttonType, 0)
                type = if (typeValue == 0) ButtonType.POSITIVE else ButtonType.NEGATIVE
            }
        }

        applyType()
        isFocusable = true
        isFocusableInTouchMode = true

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
        isAllCaps = false
    }

    private fun applyType() {

        setBackgroundResource(type.backgroundRes)

        setTextColor(
            when (type) {
                ButtonType.POSITIVE -> ContextCompat.getColor(context, R.color.anime_tv_onPrimary)
                ButtonType.NEGATIVE -> ContextCompat.getColor(context, R.color.anime_tv_onPrimary)
            },
        )
    }
}
