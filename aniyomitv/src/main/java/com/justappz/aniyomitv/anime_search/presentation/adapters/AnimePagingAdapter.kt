package com.justappz.aniyomitv.anime_search.presentation.adapters

import coil3.load
import coil3.request.crossfade
import com.justappz.aniyomitv.anime_search.domain.model.SAnimeDiffCallback
import com.justappz.aniyomitv.base.BasePagingAdapter
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
                crossfade(true) // default while loading // fallback if fails
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    },
    SAnimeDiffCallback,
)
