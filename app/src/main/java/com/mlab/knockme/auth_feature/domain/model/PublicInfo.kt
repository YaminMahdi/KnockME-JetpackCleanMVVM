package com.mlab.knockme.auth_feature.domain.model

data class PublicInfo(
    var id: String="",
    var nm: String="",
    var fbId: String="",
    var fbLink: String="",
    var pic: String="",
    var progShortName: String="",
    val batchNo: Int=0,
    var cgpa: Double=0.0,
    var ip: String="",
    var loc: String="",
)
