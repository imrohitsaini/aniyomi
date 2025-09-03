package com.justappz.aniyomitv.episodes.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.justappz.aniyomitv.playback.domain.model.EpisodeDomain

class EpisodeDiffCallback(
    private val oldList: List<EpisodeDomain>,
    private val newList: List<EpisodeDomain>,
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Assuming `url` uniquely identifies an episode
        return oldList[oldItemPosition].url == newList[newItemPosition].url
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare full object if it’s data class
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
