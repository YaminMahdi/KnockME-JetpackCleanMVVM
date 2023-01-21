package com.mlab.knockme.main_feature.domain.model

data class ViewProfileState(
    val isLoading: Boolean=true,
    val hasPrivateInfo: Boolean=false,
    val userBasicInfo: UserBasicInfo=UserBasicInfo()
)


