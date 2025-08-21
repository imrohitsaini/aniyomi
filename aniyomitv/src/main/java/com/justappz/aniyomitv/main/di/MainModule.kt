package com.justappz.aniyomitv.main.di

import com.justappz.aniyomitv.main.data.repo.TabRepositoryImpl
import com.justappz.aniyomitv.main.domain.repo.TabRepository
import com.justappz.aniyomitv.main.domain.usecase.GetMainScreenTabsUseCase
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class MainModule : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        // Repository binding
        addSingletonFactory<TabRepository> { TabRepositoryImpl() }

        // Use case binding
        addSingletonFactory { GetMainScreenTabsUseCase(get()) }
    }
}
