package com.justappz.aniyomitv.anime_search.presentation.adapters

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.anime_search.domain.model.SAnimeDiffCallback
import com.justappz.aniyomitv.base.BasePagingAdapter
import com.justappz.aniyomitv.core.util.FocusKeyHandler
import com.justappz.aniyomitv.databinding.ItemAnimeBinding
import eu.kanade.tachiyomi.animesource.model.SAnime

class AnimePagingAdapter(
) : BasePagingAdapter<SAnime, ItemAnimeBinding>(
    bindingInflater = { inflater, parent, attach ->
        ItemAnimeBinding.inflate(inflater, parent, attach)
    },
    { anime, _ ->
        tvAnimeTitle.text = anime.title
        try {
            ivAnimeThumbnail.load(anime.thumbnail_url) {
                crossfade(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    },
    SAnimeDiffCallback,
) {

    private var recyclerView: RecyclerView? = null

    fun attachRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemAnimeBinding>,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)

        val layoutManager = recyclerView?.layoutManager as GridLayoutManager
        val spanCount = layoutManager.spanCount
        val itemCount = itemCount

        // Add focus zoom effect
        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate()
                    .scaleX(1.15f)
                    .scaleY(1.15f)
                    .setDuration(200)
                    .start()
            } else {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
        }

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
