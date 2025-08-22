package com.justappz.aniyomitv.core.components.editext

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import com.justappz.aniyomitv.R

@SuppressLint("AppCompatCustomView")
class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle,
) : EditText(context, attrs, defStyleAttr) {

    init {
        // Optional: Apply default styles or attributes
        setPadding(
            resources.getDimensionPixelSize(R.dimen._8dp),
            resources.getDimensionPixelSize(R.dimen._8dp),
            resources.getDimensionPixelSize(R.dimen._8dp),
            resources.getDimensionPixelSize(R.dimen._8dp),
        )

        background =
            AppCompatResources.getDrawable(context, R.drawable.edittext_bg_selector) // optional custom background
    }
}
