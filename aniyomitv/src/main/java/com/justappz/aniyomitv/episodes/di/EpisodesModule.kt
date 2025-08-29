package com.justappz.aniyomitv.episodes.di

import com.justappz.aniyomitv.episodes.data.repo.EpisodesRepoImpl
import com.justappz.aniyomitv.episodes.domain.repo.EpisodesRepository
import com.justappz.aniyomitv.episodes.domain.usecase.GetAnimeDetailsUseCase
import com.justappz.aniyomitv.episodes.domain.usecase.GetEpisodesUseCase
import com.justappz.aniyomitv.episodes.domain.usecase.GetVideosUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class EpisodesModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingletonFactory<EpisodesRepository> { EpisodesRepoImpl() }

        // Use case binding
        addSingletonFactory { GetAnimeDetailsUseCase(get()) }
        addSingletonFactory { GetEpisodesUseCase(get()) }
        addSingletonFactory { GetVideosUseCase(get()) }
    }
}
