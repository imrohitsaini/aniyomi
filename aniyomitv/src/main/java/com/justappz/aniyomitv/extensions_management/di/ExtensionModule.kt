package com.justappz.aniyomitv.extensions_management.di

import com.justappz.aniyomitv.constants.RoomDBConstants
import com.justappz.aniyomitv.core.data.local.database.AppDatabase
import com.justappz.aniyomitv.extensions_management.data.repo.ExtensionRepoDetailsImpl
import com.justappz.aniyomitv.extensions_management.data.repo.ExtensionRepoImpl
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepo
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepoDetailsRepository
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionRepoDetailsUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import com.justappz.aniyomitv.extensions_management.domain.usecase.InsertExtensionRepoUrlUseCase
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
