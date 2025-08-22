package com.justappz.aniyomitv.extensions_management.data.repo

import android.util.Log
import com.justappz.aniyomitv.core.util.toObject
import com.justappz.aniyomitv.extensions_management.data.dto.ExtensionDTO
import com.justappz.aniyomitv.extensions_management.data.mapper.toDomain
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain
import com.justappz.aniyomitv.extensions_management.domain.repo.ExtensionRepo
import eu.kanade.tachiyomi.network.NetworkHelper
import okhttp3.Request

class ExtensionRepoImpl(
    private val networkHelper: NetworkHelper,
) : ExtensionRepo {

    companion object {
        private const val TAG = "ExtensionRepo"
    }

    override fun getExtensions(url: String): List<ExtensionDomain> {
        Log.i(TAG, "Fetching extensions from URL: $url")

        val client = networkHelper.client
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "HTTP error ${response.code}: ${response.message}")
                    throw Exception("HTTP error ${response.code}: ${response.message}")
                }

                val body = response.body.string()
                Log.i(TAG, "Response body length: ${body.length}")

                val dtoList: List<ExtensionDTO> = body.toObject()
                Log.i(TAG, "Parsed ${dtoList.size} extensions from JSON")

                return dtoList.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch extensions: ${e.message}", e)
            throw e
        }
    }

}
