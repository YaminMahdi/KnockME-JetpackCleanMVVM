package com.mlab.knockme.auth_feature.domain.model

data class PublicInfo(
    var id: String="",
    var nm: String="",
    var progShortName: String="",
    var batchNo: Int=0,
    var cgpa: Double=0.0,
    var lastUpdated: Long=0L
    )
