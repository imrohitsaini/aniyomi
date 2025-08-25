package com.justappz.aniyomitv.extensions_management.presentation.adapters

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BasePagingAdapter
import com.justappz.aniyomitv.core.util.FocusKeyHandler
import com.justappz.aniyomitv.databinding.ItemExtensionBinding
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain

@SuppressLint("SetTextI18n")
class ExtensionPagingAdapter : BasePagingAdapter<ExtensionDomain, ItemExtensionBinding>(
    bindingInflater = { inflater, parent, attach ->
        ItemExtensionBinding.inflate(inflater, parent, attach)
    },
    bind = { item, _ ->
        tvAppName.text = item.name.replace("Aniyomi: ", "")
        tvVersion.text = "${item.version}-${item.code}"

        try {
            ivAppIcon.load(item.iconUrl) {
                crossfade(true) // default while loading // fallback if fails
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (item.isInstalled) {
            ivAppIconBadge.setImageResource(R.drawable.svg_tick)
            ivAppIconBadge.isVisible = true
        } else {
            ivAppIconBadge.isVisible = false
        }

    },
    diffCallback = ExtensionDomain.DIFF_CALLBACK,
) {
    private var recyclerView: RecyclerView? = null

    fun attachRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemExtensionBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        val layoutManager = recyclerView?.layoutManager as GridLayoutManager
        val spanCount = layoutManager.spanCount
        val itemCount = itemCount

        holder.itemView.setOnKeyListener(
            FocusKeyHandler(
                onLeft = {
                    if (position % spanCount == 0) {
                        // first item in row → block
                    } else {
                        holder.itemView.focusSearch(View.FOCUS_LEFT)?.requestFocus()
                    }
                    return@FocusKeyHandler true
                },
                onRight = {
                    if ((position + 1) % spanCount == 0 || position == itemCount - 1) {
                        // last item in row or overall → block
                    } else {
                        holder.itemView.focusSearch(View.FOCUS_RIGHT)?.requestFocus()
                    }
                    return@FocusKeyHandler true
                },
                onUp = {
                    if (position < spanCount) {
                        // first row → let system handle (don’t consume)
                        return@FocusKeyHandler false
                    } else {
                        holder.itemView.focusSearch(View.FOCUS_UP)?.requestFocus()
                        return@FocusKeyHandler true
                    }
                },
                onDown = {
                    if (position + spanCount >= itemCount) {
                        // last row → block
                    } else {
                        holder.itemView.focusSearch(View.FOCUS_DOWN)?.requestFocus()
                    }
                    return@FocusKeyHandler true
                },
            ),
        )

    }
}
