package com.mlab.knockme.auth_feature.domain.model

data class ResultInfo(
    val cgpa: Double,
    val courseId: String,
    val courseTitle: String,
    val customCourseId: String,
    val gradeLetter: String,
    val grandTotal: Any,
    val pointEquivalent: Double,
    val semesterAccountsClearance: Any,
    val semesterId: String,
    val semesterName: String,
    val semesterYear: Int,
    val studentId: String,
    val teval: String,
    val tevalSubmitted: String,
    val totalCredit: Double
)
