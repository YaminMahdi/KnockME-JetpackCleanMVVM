package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SemesterInfo(
    var semesterId: String? ="",
    var semesterName: String? ="",
    var semesterYear: Int=0,
    var sgpa: Double= 0.0,
    var creditTaken: Double=0.0,
) : Parcelable{
    fun toShortSemName() = "$semesterName'${semesterYear%100}"

}