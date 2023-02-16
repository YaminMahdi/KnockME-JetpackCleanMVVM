package com.mlab.knockme.auth_feature.data.data_source.dto

import com.mlab.knockme.auth_feature.domain.model.StudentInfo

data class StudentInfoDto(
    val batchId: String,
    val batchNo: Int,
    val campusName: String,
    val departmentName: String,
    val deptShortName: String,
    val facShortName: String,
    val facultyName: String,
    val fkCampus: String,
    val progShortName: String,
    val programCredit: Int,
    val programId: String,
    val programName: String,
    val programType: String,
    val semesterId: String,
    val semesterName: String,
    val shift: String,
    val studentId: String,
    val studentName: String
){
    fun toStudentInfo()=
        StudentInfo(
            batchNo = batchNo,
            progShortName = progShortName,
            studentId = studentId,
            studentName = studentName,
            firstSemId = semesterId
        )
}