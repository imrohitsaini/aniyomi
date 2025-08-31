package com.justappz.aniyomitv.extensions.di

import com.justappz.aniyomitv.core.data.local.database.AppDatabase
import com.justappz.aniyomitv.extensions.data.repo.ExtensionRepoDetailsImpl
import com.justappz.aniyomitv.extensions.domain.repo.ExtensionRepoDetailsRepository
import com.justappz.aniyomitv.extensions.domain.usecase.ObserveExtensionsUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.GetExtensionRepoDetailsUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.InsertExtensionRepoUrlUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.RefreshExtensionsUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get


class ExtensionModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {

        // DAO
        addSingletonFactory { get<AppDatabase>().animeRepositoryDao() }

        // fetching repo url from db
        addSingletonFactory<ExtensionRepoDetailsRepository> { ExtensionRepoDetailsImpl(get(), get()) }

        // Use case binding
        addSingletonFactory { GetExtensionRepoDetailsUseCase(get()) }
        addSingletonFactory { InsertExtensionRepoUrlUseCase(get()) }
        addSingletonFactory { ObserveExtensionsUseCase(get()) }
        addSingletonFactory { RefreshExtensionsUseCase(get()) }

    }
}
