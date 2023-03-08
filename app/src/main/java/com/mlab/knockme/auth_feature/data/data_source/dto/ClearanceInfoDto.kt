package com.mlab.knockme.auth_feature.data.data_source.dto

import com.mlab.knockme.auth_feature.domain.model.ClearanceInfo

data class ClearanceInfoDto(
    val finalExam: Boolean,
    val midTermExam: Boolean,
    val registration: Boolean,
    val semesterId: String,
    val semesterName: String,
    val studentId: String
){
    fun toClearanceInfo() = ClearanceInfo(
        finalExam = finalExam,
        midTermExam = midTermExam,
        registration = registration,
        semesterId = semesterId,
        semesterName = semesterName
    )
}