package com.justappz.aniyomitv.core.components.imageview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import com.justappz.aniyomitv.R

@SuppressLint("AppCompatCustomView")
class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ImageView(context, attrs, defStyle) {

    private var cornerRadius: Float = resources.getDimension(R.dimen.item_anime_radius)

    init {
        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                if (view.width > 0 && view.height > 0) {
                    outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
                }
            }
        }
    }
}
