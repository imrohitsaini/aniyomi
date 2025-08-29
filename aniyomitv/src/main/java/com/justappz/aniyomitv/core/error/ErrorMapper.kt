package com.justappz.aniyomitv.core.error

import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMapper {
    fun fromHttp(code: Int, message: String?): AppError = when (code) {
        400 -> AppError.ValidationError(
            message ?: "Bad request",
            displayType = ErrorDisplayType.INLINE,
        )

        401 -> AppError.UnauthorizedError(
            message ?: "Not authorized to use the application",
            displayType = ErrorDisplayType.INLINE,
        )

        500 -> AppError.ServerError(
            code = code,
            message = message ?: "Something went wrong",
            displayType = ErrorDisplayType.DIALOG,
        )

        else -> AppError.UnknownError(
            message = message ?: "Something went wrong",
            displayType = ErrorDisplayType.DIALOG,
        )
    }

    fun fromException(e: Throwable): AppError = when (e) {
        is UnknownHostException -> AppError.NetworkError(
            message = "No internet connection",
            displayType = ErrorDisplayType.DIALOG,
        )

        is SocketTimeoutException -> AppError.NetworkError(
            message = "Request timed out",
            displayType = ErrorDisplayType.DIALOG,
        )

        else -> AppError.UnknownError(
            message = e.message ?: "Unexpected error",
            displayType = ErrorDisplayType.DIALOG,
        )
    }
}
