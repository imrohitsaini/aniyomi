package com.justappz.aniyomitv.search.di

import com.justappz.aniyomitv.search.data.repo.AnimeRepositoryImpl
import com.justappz.aniyomitv.search.data.repo.InstalledExtensionsRepoImpl
import com.justappz.aniyomitv.search.domain.repo.AnimeRepository
import com.justappz.aniyomitv.search.domain.repo.InstalledExtensionsRepo
import com.justappz.aniyomitv.search.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.search.domain.usecase.GetLatestAnimePagingUseCase
import com.justappz.aniyomitv.search.domain.usecase.GetPopularAnimePagingUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get


class SearchModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {


        // Fetching extensions from api
        addSingletonFactory<InstalledExtensionsRepo> { InstalledExtensionsRepoImpl() }
        addSingletonFactory<AnimeRepository> { AnimeRepositoryImpl() }


        // Use case binding
        addSingletonFactory { GetInstalledExtensionsUseCase(get()) }
        addSingletonFactory { GetPopularAnimePagingUseCase(get()) }
        addSingletonFactory { GetLatestAnimePagingUseCase(get()) }


    }
}
