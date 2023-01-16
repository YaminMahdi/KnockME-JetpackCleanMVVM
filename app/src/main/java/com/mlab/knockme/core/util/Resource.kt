package com.mlab.knockme.core.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Loading<T>(message: String,data: T? = null): Resource<T>(data, message)
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}