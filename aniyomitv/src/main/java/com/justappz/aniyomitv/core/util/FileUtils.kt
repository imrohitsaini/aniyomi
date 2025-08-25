package com.justappz.aniyomitv.core.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object FileUtils {

    /**
     * Downloads a file from [fileUrl] and saves it in cacheDir/app_extension.apk
     *
     * @param context Context to access cacheDir
     * @param fileUrl URL of the file to download
     * @return The downloaded File (or null if failed)
     */
    suspend fun downloadFile(context: Context, fileUrl: String): File? {
        val fileName = "app_extension.apk"

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext null
                }

                val file = File(context.cacheDir, fileName)

                connection.inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
