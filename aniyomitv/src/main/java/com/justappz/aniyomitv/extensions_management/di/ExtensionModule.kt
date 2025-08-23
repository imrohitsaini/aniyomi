package com.justappz.aniyomitv.extensions_management.di

import com.justappz.aniyomitv.extensions_management.data.repo.ExtensionRepoImpl
import com.justappz.aniyomitv.extensions_management.data.repo.AnimeRepositoriesDetailsImpl
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepo
import com.justappz.aniyomitv.extensions_management.domain.repo.AnimeRepositoriesDetailsRepo
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetRepoUrlsUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.RemoveRepoUrlUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.SaveRepoUrlUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get


class ExtensionModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {


        // Fetching extensions from api
        addSingletonFactory<ExtensionRepo> { ExtensionRepoImpl(get()) }

        // fetching repo url from shared prefs
        addSingletonFactory<AnimeRepositoriesDetailsRepo> { AnimeRepositoriesDetailsImpl(get()) }

        // Use case binding
        addSingletonFactory { GetExtensionUseCase(get()) }

        addSingletonFactory { GetRepoUrlsUseCase(get()) }
        addSingletonFactory { SaveRepoUrlUseCase(get()) }
        addSingletonFactory { RemoveRepoUrlUseCase(get()) }


    }
}
