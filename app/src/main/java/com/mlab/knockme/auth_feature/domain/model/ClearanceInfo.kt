package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClearanceInfo(
    val finalExam: Boolean=false,
    val midTermExam: Boolean=false,
    val registration: Boolean=false,
    val semesterId: String="",
    val semesterName: String="",
): Parcelable