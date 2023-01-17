package com.mlab.knockme.auth_feature.domain.model

data class ResultInfo(
    val courseId: String = "",
    val courseTitle: String = "",
    val customCourseId: String = "",
    val gradeLetter: String = "",
    val pointEquivalent: Double = 0.0,
    val totalCredit: Double = 0.0
)
