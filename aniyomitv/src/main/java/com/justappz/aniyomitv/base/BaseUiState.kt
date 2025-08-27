package com.justappz.aniyomitv.base

sealed class BaseUiState<out T> {
    object Idle : BaseUiState<Nothing>()
    object Loading : BaseUiState<Nothing>()
    data class Success<T>(val data: T) : BaseUiState<T>()
    object Empty : BaseUiState<Nothing>()
    data class Error(val code: Int? = null, val message: String) : BaseUiState<Nothing>()
}
