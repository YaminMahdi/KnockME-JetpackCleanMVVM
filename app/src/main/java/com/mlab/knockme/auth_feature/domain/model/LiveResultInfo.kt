package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiveResultInfo(
    val mid1: Double?=0.0,
    val mid2: Double?=0.0,
    val q1: Double?=0.0,
    val q2: Double?=0.0,
    val q3: Double?=0.0,
    val quiz: Double?=0.0,
    val customCourseId: String?="",
    val courseTitle: String?="",
    val shortSemName: String?=""

    ) : Parcelable