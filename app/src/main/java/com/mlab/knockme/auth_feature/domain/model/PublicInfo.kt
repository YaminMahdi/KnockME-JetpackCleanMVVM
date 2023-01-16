package com.mlab.knockme.auth_feature.domain.model

data class PublicInfo(
    var id: String="",
    var nm: String="",
    var progShortName: String="",
    val batchNo: Int=0,
    var cgpa: Double=0.0
)
