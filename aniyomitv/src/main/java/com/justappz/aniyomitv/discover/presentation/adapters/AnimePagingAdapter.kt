package com.justappz.aniyomitv.discover.presentation.adapters

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BasePagingAdapter
import com.justappz.aniyomitv.core.components.chips.ChipView
import com.justappz.aniyomitv.core.util.FocusKeyHandler
import com.justappz.aniyomitv.databinding.ItemAnimeBinding
import com.justappz.aniyomitv.discover.domain.model.SAnimeDiffCallback
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
    val tag = "AnimePagingAdapter"

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
                view.animate().scaleX(1.15f).scaleY(1.15f).setDuration(200).start()
            } else {
                view.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
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
                        val popularChip = recyclerView?.rootView?.findViewById<ChipView>(R.id.chipPopular)
                        val latestChip = recyclerView?.rootView?.findViewById<ChipView>(R.id.chipLatest)
                        if (popularChip?.getSelectedState() == true) {
                            popularChip.requestFocus()
                        } else {
                            latestChip?.requestFocus()
                        }
                        true
                    } else {
                        holder.itemView.focusSearch(View.FOCUS_UP)?.requestFocus()
                        true
                    }
                },
                onDown = {
                    val nextPos = position + spanCount
                    val totalCount = itemCount

                    if (nextPos < totalCount) {
                        val nextHolder = recyclerView?.findViewHolderForAdapterPosition(nextPos)

                        if (nextHolder != null) {
                            // ✅ Already bound → focus directly
                            nextHolder.itemView.requestFocus()
                        } else {
                            // ✅ Scroll (ensures Paging prefetch kicks in)
                            recyclerView?.scrollToPosition(nextPos)

                            // ✅ Watch for either new items or child attachment
                            val adapter = recyclerView?.adapter
                            val dataObserver = object : RecyclerView.AdapterDataObserver() {
                                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                                    if (nextPos in positionStart until (positionStart + itemCount)) {
                                        recyclerView?.post {
                                            recyclerView?.findViewHolderForAdapterPosition(nextPos)?.itemView?.requestFocus()
                                        }
                                        adapter?.unregisterAdapterDataObserver(this)
                                    }
                                }
                            }
                            adapter?.registerAdapterDataObserver(dataObserver)

                            recyclerView?.addOnChildAttachStateChangeListener(
                                object : RecyclerView.OnChildAttachStateChangeListener {
                                    override fun onChildViewAttachedToWindow(view: View) {
                                        val vh = recyclerView?.getChildViewHolder(view)
                                        if (vh?.bindingAdapterPosition == nextPos) {
                                            view.requestFocus()
                                            recyclerView?.removeOnChildAttachStateChangeListener(this)
                                        }
                                    }

                                    override fun onChildViewDetachedFromWindow(view: View) = Unit
                                },
                            )
                        }
                        true
                    } else {
                        true // already last row → block
                    }
                },
            ),
        )
    }
}
