package com.justappz.aniyomitv.playback.di

import com.justappz.aniyomitv.core.data.local.database.AppDatabase
import com.justappz.aniyomitv.playback.data.repo.AnimeEpisodeRepoImpl
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo
import com.justappz.aniyomitv.playback.domain.usecase.UpdateAnimeWithDbUseCase
import com.justappz.aniyomitv.playback.domain.usecase.UpdateEpisodeWithDbUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class PlaybackModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        // DAO
        addSingletonFactory { get<AppDatabase>().animeEpisodeDao() }

        // REPO
        addSingletonFactory<AnimeEpisodeRepo> { AnimeEpisodeRepoImpl(get()) }

        // USE CASES
        addSingletonFactory { UpdateAnimeWithDbUseCase(get()) }
        addSingletonFactory { UpdateEpisodeWithDbUseCase(get()) }
    }
}
