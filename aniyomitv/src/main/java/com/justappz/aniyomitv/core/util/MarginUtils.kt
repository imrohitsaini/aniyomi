package com.justappz.aniyomitv.core.util

import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.core.view.updateLayoutParams

// Extension to update margins using dimension resources
fun View.updateMarginRes(
    @DimenRes start: Int? = null,
    @DimenRes top: Int? = null,
    @DimenRes end: Int? = null,
    @DimenRes bottom: Int? = null,
) {
    if (layoutParams !is ViewGroup.MarginLayoutParams) return
    val context = this.context

    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        marginStart = start?.let { context.resources.getDimensionPixelSize(it) } ?: marginStart
        topMargin = top?.let { context.resources.getDimensionPixelSize(it) } ?: topMargin
        marginEnd = end?.let { context.resources.getDimensionPixelSize(it) } ?: marginEnd
        bottomMargin = bottom?.let { context.resources.getDimensionPixelSize(it) } ?: bottomMargin
    }
}

// Convenience extensions for individual sides
fun View.setMarginStartRes(@DimenRes value: Int) = updateMarginRes(start = value)
fun View.setMarginTopRes(@DimenRes value: Int) = updateMarginRes(top = value)
fun View.setMarginEndRes(@DimenRes value: Int) = updateMarginRes(end = value)
fun View.setMarginBottomRes(@DimenRes value: Int) = updateMarginRes(bottom = value)
