package com.mlab.knockme.auth_feature.domain.model

import com.mlab.knockme.core.util.orZero

data class StudentInfo(
    val batchNo: Int = 0,
    val studentId: String = "",
    val studentName: String = "",
    val firstSemId: String = "0",
    val programId: String = "",
    val programName: String = "",
    val progShortName: String = ""
) {
    fun toPublicInfo(
        cgpa: Double = 0.0,
        totalCompletedCredit: Double = 0.0
    ) = PublicInfo (
        id = studentId,
        nm = studentName,
        batchNo = batchNo,
        cgpa = cgpa,
        totalCompletedCredit = totalCompletedCredit,
        firstSemId = firstSemId.toIntOrNull().orZero(),
        programId = programId,
        programName = programName,
        progShortName = progShortName
    )
}
