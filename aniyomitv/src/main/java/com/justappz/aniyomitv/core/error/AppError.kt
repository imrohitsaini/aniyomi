package com.justappz.aniyomitv.core.error

enum class ErrorDisplayType {
    TOAST,
    INLINE,
    DIALOG
}

sealed class AppError(
    val message: String,
    val displayType: ErrorDisplayType = ErrorDisplayType.TOAST, // default if not passed
) {
    class NetworkError(
        message: String,
        displayType: ErrorDisplayType? = null,
    ) : AppError(message, displayType ?: ErrorDisplayType.TOAST)

    class ServerError(
        val code: Int,
        message: String,
        displayType: ErrorDisplayType? = null,
    ) : AppError(message, displayType ?: ErrorDisplayType.DIALOG)

    class UnauthorizedError(
        message: String = "Unauthorized",
        displayType: ErrorDisplayType? = null,
    ) : AppError(message, displayType ?: ErrorDisplayType.TOAST)

    class ValidationError(
        message: String,
        displayType: ErrorDisplayType? = null,
    ) : AppError(message, displayType ?: ErrorDisplayType.INLINE)

    class UnknownError(
        message: String = "Something went wrong",
        displayType: ErrorDisplayType? = null,
    ) : AppError(message, displayType ?: ErrorDisplayType.TOAST)
}
