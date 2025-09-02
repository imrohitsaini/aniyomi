package com.justappz.aniyomitv.playback.domain.repo

import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.playback.domain.model.AnimeDomain

interface AnimeEpisodeRepo {
    //repos
    suspend fun updateAnimeWithDb(animeDomain: AnimeDomain): BaseUiState<AnimeDomain>
}
