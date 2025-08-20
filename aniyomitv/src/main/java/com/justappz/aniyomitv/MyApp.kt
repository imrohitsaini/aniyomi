package com.justappz.aniyomitv

import android.app.Application
import com.justappz.aniyomitv.di.AppModule
import com.justappz.aniyomitv.di.PreferenceModule
import dev.mihon.injekt.patchInjekt
import uy.kohesive.injekt.Injekt

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        patchInjekt()
        Injekt.importModule(AppModule(this))
        Injekt.importModule(PreferenceModule(this))

    }
}
