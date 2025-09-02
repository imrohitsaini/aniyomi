package com.justappz.aniyomitv.core.di

import android.app.Application
import androidx.room.Room
import com.justappz.aniyomitv.constants.RoomDBConstants
import com.justappz.aniyomitv.core.data.local.database.AppDatabase
import eu.kanade.tachiyomi.network.NetworkHelper
import kotlinx.serialization.json.Json
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory
import uy.kohesive.injekt.api.get

class AppModule(val app: Application) : InjektModule {
    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)
        addSingletonFactory { NetworkHelper(app, get()) }
        addSingletonFactory {
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        }

        addSingletonFactory {
            Room.databaseBuilder(
                app,
                AppDatabase::class.java,
                RoomDBConstants.DATABASE
            ).fallbackToDestructiveMigration(true).build()
        }
    }
}
