package com.example.transfergo.util

sealed class FxResult<out T> {
    data class Success<out T>(val data: T) : FxResult<T>()
    data class Error(val exception: Throwable) : FxResult<Nothing>()
}
