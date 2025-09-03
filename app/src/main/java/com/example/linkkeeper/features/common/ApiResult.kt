package com.example.linkkeeper.features.common


sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val exception: Exception) : ApiResult<T>()
    class Loading<T> : ApiResult<T>()
}