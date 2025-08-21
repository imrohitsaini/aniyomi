package com.justappz.aniyomitv.di

import android.app.Application
import com.justappz.aniyomitv.core.util.system.isDebugBuildType
import eu.kanade.tachiyomi.network.NetworkPreferences
import tachiyomi.core.common.preference.AndroidPreferenceStore
import tachiyomi.core.common.preference.PreferenceStore
import tachiyomi.core.common.storage.AndroidStorageFolderProvider
import tachiyomi.domain.storage.service.StoragePreferences
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class PreferenceModule(val app: Application) : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingletonFactory<PreferenceStore> {
            AndroidPreferenceStore(app)
        }
        addSingletonFactory {
            NetworkPreferences(
                preferenceStore = get(),
                verboseLogging = isDebugBuildType,
            )
        }
        addSingletonFactory {
            StoragePreferences(
                folderProvider = get<AndroidStorageFolderProvider>(),
                preferenceStore = get(),
            )
        }
    }
}
