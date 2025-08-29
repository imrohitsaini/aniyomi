package com.justappz.aniyomitv.base

import com.justappz.aniyomitv.core.error.AppError

sealed class BaseUiState<out T> {
    object Idle : BaseUiState<Nothing>()
    object Loading : BaseUiState<Nothing>()
    data class Success<T>(val data: T) : BaseUiState<T>()
    object Empty : BaseUiState<Nothing>()
    data class Error(val error: AppError) : BaseUiState<Nothing>()
}
