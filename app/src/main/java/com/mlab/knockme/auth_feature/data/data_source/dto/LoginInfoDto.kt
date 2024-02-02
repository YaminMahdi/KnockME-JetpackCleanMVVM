package com.mlab.knockme.auth_feature.data.data_source.dto

data class LoginInfoDto(
    val accessToken: String,
    val commaSeparatedRoles: String,
    val deviceName: String,
    val message: String,
    val responseMessage: String,
    val name: String,
    val userName: String
)