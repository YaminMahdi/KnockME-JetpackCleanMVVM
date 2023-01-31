package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseInfo(
    val courseSectionId: Int?=0,
    val courseTitle: String?="",
    val customCourseId: String?="",
    val employeeName: String?="",
    val sectionName: String?="",
    val semesterId: String?="",
    val semesterName: String?="",
    val semesterYear: Int?=0,
    val totalCredit: Double?=0.0
) : Parcelable {
    fun toShortSemName() = "$semesterName'${semesterYear!!%100}"
}

