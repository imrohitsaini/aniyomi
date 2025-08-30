package com.justappz.aniyomitv.extensions.data.repo

import android.util.Log
import com.justappz.aniyomitv.base.BaseUiState
import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.util.toObject
import com.justappz.aniyomitv.extensions.data.dto.ExtensionDTO
import com.justappz.aniyomitv.extensions.data.mapper.toDomain
import com.justappz.aniyomitv.extensions.domain.model.RepoDomain
import com.justappz.aniyomitv.extensions.domain.repo.ExtensionRepo
import eu.kanade.tachiyomi.network.NetworkHelper
import okhttp3.Request

class ExtensionRepoImpl(
    private val networkHelper: NetworkHelper,
) : ExtensionRepo {

    companion object {
        private const val TAG = "ExtensionRepo"
    }

    override fun getExtensions(url: String): BaseUiState<RepoDomain> {
        Log.i(TAG, "Fetching extensions from URL: $url")

        val client = networkHelper.client
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "HTTP error ${response.code}: ${response.message}")
                    return BaseUiState.Error(
                        AppError.ServerError(
                            code = response.code,
                            message = response.message.ifBlank { "HTTP error ${response.code}" },
                        ),
                    )
                }

                val body = response.body.string()

                Log.i(TAG, "Response body length: ${body.length}")

                val dtoList: List<ExtensionDTO> = try {
                    body.toObject()
                } catch (e: Exception) {
                    Log.e(TAG, "JSON parsing failed: ${e.message}", e)
                    return BaseUiState.Error(
                        AppError.ValidationError("Invalid response format"),
                    )
                }

                Log.i(TAG, "Parsed ${dtoList.size} extensions from JSON")

                if (dtoList.isEmpty()) {
                    return BaseUiState.Empty
                }

                BaseUiState.Success(
                    RepoDomain(
                        repoUrl = url,
                        extensions = dtoList.map { it.toDomain(url) },
                    ),
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch extensions: ${e.message}", e)
            BaseUiState.Error(
                AppError.NetworkError(
                    message = e.message ?: "Failed to fetch extensions",
                ),
            )
        }
    }

}
