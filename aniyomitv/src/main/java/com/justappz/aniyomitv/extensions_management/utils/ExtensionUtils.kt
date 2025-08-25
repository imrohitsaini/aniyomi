package com.justappz.aniyomitv.extensions_management.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.justappz.aniyomitv.extensions_management.domain.model.InstalledExtensionInfo
import java.io.File

object ExtensionUtils {

    /**
     * Checks whether the given package is installed on the device and returns details.
     *
     * @param context Context to access PackageManager
     * @param packageName The package name to check
     * @return InstalledExtension with installed flag and version details
     */
    @Suppress("DEPRECATION")
    fun getInstalledExtension(context: Context, packageName: String): InstalledExtensionInfo {
        return try {
            val pkgInfo = context.packageManager.getPackageInfo(packageName, 0)

            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkgInfo.longVersionCode // API 28+
            } else {
                pkgInfo.versionCode.toLong() // Deprecated but works on <28
            }

            InstalledExtensionInfo(
                installed = true,
                installedVersionCode = versionCode.toInt(),
                installedVersionName = pkgInfo.versionName ?: "",
            )
        } catch (e: PackageManager.NameNotFoundException) {
            InstalledExtensionInfo(installed = false)
        }
    }

    /**
     * Install an APK via PackageInstaller with callback.
     *
     * @param context App context
     * @param file The APK file
     */
    fun installExtension(context: Context, file: File) {
        val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    fun uninstallExtension(context: Context, packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = "package:$packageName".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
