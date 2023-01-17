package com.mlab.knockme.auth_feature.data.data_source.dto

import com.mlab.knockme.auth_feature.domain.model.ResultInfo
import com.mlab.knockme.auth_feature.domain.model.SemesterInfo

data class ResultInfoDto(
    val blockCause: Any,
    val blocked: String,
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
){
    fun toResultInfo()=
        ResultInfo(courseId,
            courseTitle = courseTitle,
            customCourseId = customCourseId,
            gradeLetter = gradeLetter,
            pointEquivalent = pointEquivalent,
            totalCredit = totalCredit
        )

    fun toSemesterInfo(creditTaken:Double)=
        SemesterInfo(
            semesterId = semesterId,
            semesterName = semesterName,
            semesterYear = semesterYear,
            sgpa = cgpa,
            creditTaken = creditTaken
        )
}