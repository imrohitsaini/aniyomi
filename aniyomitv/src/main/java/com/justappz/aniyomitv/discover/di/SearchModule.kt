package com.justappz.aniyomitv.discover.di

import com.justappz.aniyomitv.discover.data.repo.AnimeRepositoryImpl
import com.justappz.aniyomitv.discover.data.repo.InstalledExtensionsRepoImpl
import com.justappz.aniyomitv.discover.domain.repo.AnimeRepository
import com.justappz.aniyomitv.discover.domain.repo.InstalledExtensionsRepo
import com.justappz.aniyomitv.discover.domain.usecase.GetInstalledExtensionsUseCase
import com.justappz.aniyomitv.discover.domain.usecase.GetLatestAnimePagingUseCase
import com.justappz.aniyomitv.discover.domain.usecase.GetPopularAnimePagingUseCase
import com.justappz.aniyomitv.discover.domain.usecase.SearchAnimePagingUseCase
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
        addSingletonFactory { SearchAnimePagingUseCase(get()) }


    }
}
