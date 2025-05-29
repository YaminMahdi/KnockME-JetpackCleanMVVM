package com.mlab.knockme.auth_feature.domain.model

import com.facebook.AccessToken

data class SocialAuthInfo(
    val accessToken: AccessToken?,
    var userId: String = "",
    var fbLink: String = "",
    var pic: String = ""
)