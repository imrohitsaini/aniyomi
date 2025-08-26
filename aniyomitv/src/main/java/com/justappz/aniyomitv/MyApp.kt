package com.justappz.aniyomitv

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.allowRgb565
import coil3.request.crossfade
import com.justappz.aniyomitv.anime_search.di.SearchModule
import com.justappz.aniyomitv.constants.LogKeys
import com.justappz.aniyomitv.core.di.AppModule
import com.justappz.aniyomitv.core.di.PreferenceModule
import com.justappz.aniyomitv.extensions_management.di.ExtensionModule
import com.justappz.aniyomitv.main.di.MainModule
import dev.mihon.injekt.patchInjekt
import uy.kohesive.injekt.Injekt

class MyApp : Application(), Application.ActivityLifecycleCallbacks, SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        val start = System.currentTimeMillis()

        patchInjekt()
        Injekt.importModule(AppModule(this))
        Injekt.importModule(PreferenceModule(this))
        Injekt.importModule(MainModule())
        Injekt.importModule(ExtensionModule())
        Injekt.importModule(SearchModule())

        Log.d("MyApp", "Injekt modules initialized in ${System.currentTimeMillis() - start} ms")

        registerActivityLifecycleCallbacks(this)
    }


    //region Activity Lifecycle Callbacks
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(LogKeys.ACTIVITY_LIFECYCLE, "${activity.localClassName} created")
        registerFragmentCallbacksIfPossible(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(LogKeys.ACTIVITY_LIFECYCLE, "${activity.localClassName} started")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(LogKeys.ACTIVITY_LIFECYCLE, "${activity.localClassName} resumed")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(LogKeys.ACTIVITY_LIFECYCLE, "${activity.localClassName} paused")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(LogKeys.ACTIVITY_LIFECYCLE, "${activity.localClassName} stopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(LogKeys.ACTIVITY_LIFECYCLE, "${activity.localClassName} saveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(LogKeys.ACTIVITY_LIFECYCLE, "${activity.localClassName} destroyed")
    }
    //endregion

    //region Fragment Lifecycle Callbacks
    private fun registerFragmentCallbacksIfPossible(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentCreated(
                        fm: FragmentManager,
                        f: Fragment,
                        savedInstanceState: Bundle?,
                    ) {
                        Log.d(
                            LogKeys.ACTIVITY_LIFECYCLE,
                            "${f::class.java.simpleName} fragment created in ${activity.localClassName}",
                        )
                    }

                    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                        Log.d(
                            LogKeys.ACTIVITY_LIFECYCLE,
                            "${f::class.java.simpleName} fragment resumed in ${activity.localClassName}",
                        )
                    }

                    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
                        Log.d(
                            LogKeys.ACTIVITY_LIFECYCLE,
                            "${f::class.java.simpleName} fragment paused in ${activity.localClassName}",
                        )
                    }

                    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                        Log.d(
                            LogKeys.ACTIVITY_LIFECYCLE,
                            "${f::class.java.simpleName} fragment destroyed in ${activity.localClassName}",
                        )
                    }
                },
                true,
            )
        }
    }
    //endregion

    //region newImageLoader
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .allowRgb565(true) // optional memory optimization
            .build()
    }
    //endregion
}
