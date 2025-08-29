package com.justappz.aniyomitv.episodes.presentation.adapters

import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.databinding.ItemEpisodesBinding
import eu.kanade.tachiyomi.animesource.model.SEpisode

class EpisodesAdapter(items: List<SEpisode>) : BaseRecyclerViewAdapter<SEpisode, ItemEpisodesBinding>(
    items = items,
    bindingInflater = { inflater, parent, attach ->
        ItemEpisodesBinding.inflate(inflater, parent, attach)
    },
    { episode, _ ->
        // Format episode number: drop .0 if it's an integer
        val formattedNumber = episode.episode_number.toDouble()
        val displayNumber = if (formattedNumber % 1.0 == 0.0) {
            formattedNumber.toInt().toString()
        } else {
            formattedNumber.toString()
        }
        tvEpisodeNumber.text = displayNumber
    },
)
