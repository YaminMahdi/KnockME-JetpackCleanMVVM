package com.mlab.knockme.auth_feature.data.data_source.dto

import com.google.gson.annotations.SerializedName
import com.mlab.knockme.auth_feature.domain.model.StudentInfo

data class StudentInfoDto(
    @SerializedName("batchId") val batchId: String?,
    @SerializedName("batchNo") val batchNo: Int?,
    @SerializedName("campusName") val campusName: String?,
    @SerializedName("departmentName") val departmentName: String?,
    @SerializedName("deptShortName") val deptShortName: String?,
    @SerializedName("facShortName") val facShortName: String?,
    @SerializedName("facultyName") val facultyName: String?,
    @SerializedName("fkCampus") val fkCampus: String?,
    @SerializedName("progShortName") val progShortName: String?,
    @SerializedName("programCredit") val programCredit: Int?,
    @SerializedName("programId") val programId: String?,
    @SerializedName("programName") val programName: String?,
    @SerializedName("programType") val programType: String?,
    @SerializedName("semesterId") val semesterId: String?,
    @SerializedName("semesterName") val semesterName: String?,
    @SerializedName("shift") val shift: String?,
    @SerializedName("studentId") val studentId: String?,
    @SerializedName("studentName") val studentName: String?
) {
    fun toStudentInfo() = StudentInfo(
        batchNo = batchNo ?: 0, // Example: default to 0 if null
        programName = programName ?: "", // Example: default to empty string
        progShortName = progShortName ?: "", // Example: default to empty string
        studentId = studentId ?: "",
        studentName = studentName ?: "",
        firstSemId = semesterId ?: "",
        programId = programId ?: "",
    )
}