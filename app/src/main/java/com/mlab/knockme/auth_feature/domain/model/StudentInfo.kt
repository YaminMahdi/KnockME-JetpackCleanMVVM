package com.mlab.knockme.auth_feature.domain.model

data class StudentInfo(
    val batchNo: Int? = 0,
    val progShortName: String? = "",
    val studentId: String = "",
    val studentName: String? = "",
    val firstSemId: String? = "0"
)
