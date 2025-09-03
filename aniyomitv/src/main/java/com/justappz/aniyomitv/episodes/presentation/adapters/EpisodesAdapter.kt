package com.justappz.aniyomitv.episodes.presentation.adapters

import androidx.core.view.isVisible
import com.justappz.aniyomitv.R
import com.justappz.aniyomitv.base.BaseRecyclerViewAdapter
import com.justappz.aniyomitv.constants.EpisodeWatchState
import com.justappz.aniyomitv.databinding.ItemEpisodesBinding
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain

class EpisodesAdapter(items: List<EpisodeDomain>) : BaseRecyclerViewAdapter<EpisodeDomain, ItemEpisodesBinding>(
    items = items,
    bindingInflater = { inflater, parent, attach ->
        ItemEpisodesBinding.inflate(inflater, parent, attach)
    },
    { episode, _ ->
        // Format episode number: drop .0 if it's an integer
        val formattedNumber = episode.episodeNumber.toDouble()
        val displayNumber = if (formattedNumber % 1.0 == 0.0) {
            formattedNumber.toInt().toString()
        } else {
            formattedNumber.toString()
        }
        ivEpisodeBadge.isVisible = false
        if (episode.watchState == EpisodeWatchState.WATCHED) {
            ivEpisodeBadge.isVisible = true
            ivEpisodeBadge.setImageResource(R.drawable.svg_tick)
        } else if (episode.watchState == EpisodeWatchState.IN_PROGRESS) {
            ivEpisodeBadge.isVisible = true
            ivEpisodeBadge.setImageResource(R.drawable.svg_play)
        }

        tvEpisodeNumber.text = displayNumber
    },
)
