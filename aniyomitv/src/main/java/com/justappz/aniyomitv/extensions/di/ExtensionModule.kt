package com.justappz.aniyomitv.extensions.di

import com.justappz.aniyomitv.core.data.local.database.AppDatabase
import com.justappz.aniyomitv.extensions.data.repo.ExtensionRepoDetailsImpl
import com.justappz.aniyomitv.extensions.data.repo.ExtensionRepoImpl
import com.justappz.aniyomitv.extensions.domain.repo.ExtensionRepo
import com.justappz.aniyomitv.extensions.domain.repo.ExtensionRepoDetailsRepository
import com.justappz.aniyomitv.extensions.domain.usecase.GetExtensionRepoDetailsUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions.domain.usecase.InsertExtensionRepoUrlUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get


class ExtensionModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {

        // DAO
        addSingletonFactory { get<AppDatabase>().animeRepositoryDao() }

        // fetching repo url from db
        addSingletonFactory<ExtensionRepoDetailsRepository> { ExtensionRepoDetailsImpl(get()) }

        // Fetching extensions from api
        addSingletonFactory<ExtensionRepo> { ExtensionRepoImpl(get()) }

        // Use case binding
        addSingletonFactory { GetExtensionUseCase(get()) }

        addSingletonFactory { GetExtensionRepoDetailsUseCase(get()) }
        addSingletonFactory { InsertExtensionRepoUrlUseCase(get()) }

    }
}
