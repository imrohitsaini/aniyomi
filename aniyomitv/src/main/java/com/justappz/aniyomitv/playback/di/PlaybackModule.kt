package com.justappz.aniyomitv.playback.di

import com.justappz.aniyomitv.database.repo.DatabaseRepoImpl
import com.justappz.aniyomitv.playback.domain.repo.AnimeEpisodeRepo
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class PlaybackModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        // REPO
        addSingletonFactory<AnimeEpisodeRepo> { DatabaseRepoImpl(get()) }
    }
}
