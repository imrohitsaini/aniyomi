package com.justappz.aniyomitv.extensions_management.di

import com.justappz.aniyomitv.extensions_management.data.repo.ExtensionRepoImpl
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepo
import com.justappz.aniyomitv.extensions_management.domain.usecase.GetExtensionUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get


class ExtensionModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        // NetworkHelper should already be provided elsewhere
        addSingletonFactory<ExtensionRepo> { ExtensionRepoImpl(get()) }

        // Use case binding
        addSingletonFactory { GetExtensionUseCase(get()) }
    }
}
