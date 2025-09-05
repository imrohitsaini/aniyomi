package com.justappz.aniyomitv.core.util

import com.justappz.aniyomitv.core.error.AppError
import com.justappz.aniyomitv.core.error.ErrorDisplayType

object UserDefinedErrors {

    val MISSING_EXTENSION = AppError.UnknownError(
        message = "Extension is missing",
        displayType = ErrorDisplayType.TOAST,
    )

    val UNABLE_TO_LOAD_EXTENSION = AppError.UnknownError(
        message = "Unable to load extension",
        displayType = ErrorDisplayType.TOAST,
    )

    val SOMETHING_WENT_WRONG = AppError.UnknownError(
        message = "Something went wrong",
        displayType = ErrorDisplayType.TOAST,
    )
}
