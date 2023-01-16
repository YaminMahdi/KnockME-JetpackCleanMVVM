package com.mlab.knockme.auth_feature.domain.model

data class PrivateInfoExtended(
    var fbId: String="",
    var fbLink: String="",
    var pic: String="",
    var bloodGroup: String?="",
    var email: String?="",
    var permanentHouse: String?="",
    var ip: String="",
    var loc: String="",
)
