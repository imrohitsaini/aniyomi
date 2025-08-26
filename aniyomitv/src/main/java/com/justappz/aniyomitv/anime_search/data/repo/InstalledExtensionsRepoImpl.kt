package com.justappz.aniyomitv.anime_search.data.repo

import android.content.Context
import android.content.pm.PackageManager
import com.justappz.aniyomitv.anime_search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.anime_search.domain.repo.InstalledExtensionsRepo
import dalvik.system.PathClassLoader
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

class InstalledExtensionsRepoImpl : InstalledExtensionsRepo {
    override fun getInstalledExtensions(context: Context): List<InstalledExtensions> {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)

        return packages.mapNotNull { pkg ->
            val info = try {
                pm.getPackageInfo(
                    pkg.packageName,
                    PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES,
                )
            } catch (e: Exception) {
                return@mapNotNull null
            }

            val meta = info.applicationInfo?.metaData ?: return@mapNotNull null
            val className = meta.getString("tachiyomi.animeextension.class") ?: return@mapNotNull null
            val nsfwFlag = meta.getInt("tachiyomi.animeextension.nsfw", 0)
            val pkgName = info.packageName
            val fullClassName = if (className.startsWith(".")) pkgName + className else className

            // Try loading instance
            val instance = try {
                val pathLoader = PathClassLoader(
                    info.applicationInfo?.sourceDir,
                    context.classLoader,
                )
                val clazz = pathLoader.loadClass(fullClassName)
                val newInstance = clazz.getDeclaredConstructor().newInstance()
                newInstance as? AnimeHttpSource
            } catch (e: Exception) {
                null
            }

            InstalledExtensions(
                packageName = pkgName,
                className = fullClassName,
                nsfwFlag = nsfwFlag,
                instance = instance,
            )
        }
    }
}
