package com.justappz.aniyomitv.core.components.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view) // item position

        // Only apply spacing between columns, no edge spacing
        outRect.left = spacing / 2
        outRect.right = spacing / 2
        outRect.top = spacing
        // optional: add bottom spacing if needed
        outRect.bottom = 0
    }
}
