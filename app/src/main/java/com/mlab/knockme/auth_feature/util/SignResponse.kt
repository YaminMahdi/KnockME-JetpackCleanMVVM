package com.mlab.knockme.auth_feature.util

sealed class  SignResponse<out T>{
    object Loading: SignResponse<Nothing>()

    data class Success<out T>(
        val data: T
    ): SignResponse<T>()

    data class Error(
        val msg:String
    ): SignResponse<Nothing>()

}
