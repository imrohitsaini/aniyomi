package com.justappz.aniyomitv.search.data.repo

import android.content.Context
import android.content.pm.PackageManager
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType
import com.justappz.aniyomitv.core.error.ErrorMapper
import com.justappz.aniyomitv.search.domain.model.InstalledExtensions
import com.justappz.aniyomitv.search.domain.repo.InstalledExtensionsRepo
import dalvik.system.PathClassLoader
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource

class InstalledExtensionsRepoImpl : InstalledExtensionsRepo {
    override fun getInstalledExtensions(context: Context): BaseUiState<List<InstalledExtensions>> {
        return try {
            val pm = context.packageManager
            val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)

            val extensions = packages.mapNotNull { pkg ->
                try {
                    val info = pm.getPackageInfo(
                        pkg.packageName,
                        PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES,
                    )

                    val meta = info.applicationInfo?.metaData ?: return@mapNotNull null
                    val className = meta.getString("tachiyomi.animeextension.class")
                        ?: return@mapNotNull null

                    val nsfwFlag = meta.getInt("tachiyomi.animeextension.nsfw", 0)
                    val pkgName = info.packageName
                    val fullClassName =
                        if (className.startsWith(".")) pkgName + className else className

                    val appName = pm.getApplicationLabel(info.applicationInfo!!).toString()

                    val instance = try {
                        val pathLoader = PathClassLoader(
                            info.applicationInfo?.sourceDir,
                            context.classLoader,
                        )
                        val clazz = pathLoader.loadClass(fullClassName)
                        val newInstance = clazz.getDeclaredConstructor().newInstance()
                        newInstance as? AnimeHttpSource
                    } catch (e: Exception) {
                        return BaseUiState.Error(
                            AppError.UnknownError(
                                message = e.message ?: "Something went wrong!",
                                displayType = ErrorDisplayType.INLINE,
                            ),
                        )
                    }

                    InstalledExtensions(
                        packageName = pkgName,
                        className = fullClassName,
                        nsfwFlag = nsfwFlag,
                        instance = instance,
                        appName = appName.replace("Aniyomi: ", ""),
                    )
                } catch (e: Exception) {
                    return BaseUiState.Error(
                        AppError.UnknownError(
                            message = e.message ?: "Something went wrong!",
                            displayType = ErrorDisplayType.INLINE,
                        ),
                    )
                }
            }

            if (extensions.isEmpty()) {
                BaseUiState.Empty
            } else {
                BaseUiState.Success(extensions)
            }
        } catch (e: Exception) {
            // Any top-level exception goes through ErrorMapper
            BaseUiState.Error(ErrorMapper.fromException(e))
        }
    }
}
