package com.justappz.aniyomitv.extensions_management.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File

object ExtensionUtils {

    /**
     * Checks whether the given package is installed on the device.
     *
     * @param context Context to access PackageManager
     * @param packageName The package name to check
     * @return true if installed, false otherwise
     */
    fun isExtensionInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
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
