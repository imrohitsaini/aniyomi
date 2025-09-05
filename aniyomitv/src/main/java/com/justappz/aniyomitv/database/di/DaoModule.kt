package com.justappz.aniyomitv.database.di

import com.justappz.aniyomitv.database.AppDatabase
import com.justappz.aniyomitv.database.usecase.GetAllEpisodesForAnime
import com.justappz.aniyomitv.database.usecase.UpdateAnimeWithDbUseCase
import com.justappz.aniyomitv.database.usecase.UpdateEpisodeWithDbUseCase
import com.justappz.aniyomitv.library.domain.usecase.GetAnimeInLibraryUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class DaoModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        // DAO
        addSingletonFactory { get<AppDatabase>().animeEpisodeDao() }

        // USE CASES
        addSingletonFactory { UpdateAnimeWithDbUseCase(get()) }
        addSingletonFactory { UpdateEpisodeWithDbUseCase(get()) }
        addSingletonFactory { GetAnimeInLibraryUseCase(get()) }
        addSingletonFactory { GetAllEpisodesForAnime(get()) }

    }
}
