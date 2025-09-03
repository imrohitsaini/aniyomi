package com.justappz.aniyomitv.library.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.databinding.ItemAnimeBinding
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain

class AnimeLibraryAdapter(
    var items: List<AnimeDomain>,
) : BaseRecyclerViewAdapter<AnimeDomain, ItemAnimeBinding>(
    items = items,
    bindingInflater = { inflater, parent, attach ->
        ItemAnimeBinding.inflate(inflater, parent, attach)
    },
    { anime, _ ->
        tvAnimeTitle.text = anime.title
        try {
            ivAnimeThumbnail.load(anime.thumbnailUrl) {
                crossfade(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    },
) {
    private var recyclerView: RecyclerView? = null

    fun attachRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }
}
