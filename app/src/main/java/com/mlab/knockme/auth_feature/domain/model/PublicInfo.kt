package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PublicInfo(
    var id: String = "",
    var nm: String = "",
    var batchNo: Int = 0,
    var cgpa: Double = 0.0,
    var totalCompletedCredit: Double = 0.0,
    var firstSemId: Int = 0,
    val programId: String = "",
    val programName: String = "",
    val progShortName: String = ""
) : Parcelable{
    fun toProgramInfo() = ProgramInfo(
        programId = programId,
        shortName = progShortName,
        name = programName
    )
}
