package com.justappz.aniyomitv.discover.domain.model

import androidx.recyclerview.widget.DiffUtil
import eu.kanade.tachiyomi.animesource.model.SAnime

/**
 * DiffUtil for comparing SAnime items in PagingDataAdapter
 */
object SAnimeDiffCallback : DiffUtil.ItemCallback<SAnime>() {
    override fun areItemsTheSame(oldItem: SAnime, newItem: SAnime): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: SAnime, newItem: SAnime): Boolean {
        return oldItem.url == newItem.url &&
            oldItem.title == newItem.title &&
            oldItem.thumbnail_url == newItem.thumbnail_url
    }
}
