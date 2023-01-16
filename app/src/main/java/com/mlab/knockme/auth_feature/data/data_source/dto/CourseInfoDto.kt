package com.mlab.knockme.auth_feature.data.data_source.dto

import com.mlab.knockme.auth_feature.domain.model.CourseInfo

data class CourseInfoDto(
    val advisedStatus: String,
    val courseSectionId: Int,
    val courseTitle: String,
    val customCourseId: String,
    val designation: String,
    val employeeName: String,
    val regClearenc: String,
    val sectionName: String,
    val semesterId: String,
    val semesterName: String,
    val semesterYear: Int,
    val studentId: String,
    val totalCredit: Double
) {

    fun toCourseInfo() =
        CourseInfo(
            courseSectionId = courseSectionId,
            courseTitle = courseTitle,
            customCourseId = customCourseId,
            employeeName = employeeName,
            sectionName = sectionName,
            semesterId = semesterId,
            semesterName = semesterName,
            semesterYear = semesterYear,
            totalCredit = totalCredit
        )
}