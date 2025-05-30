package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FullResultInfo(
    var semesterInfo: SemesterInfo = SemesterInfo(),
    var resultInfo: ArrayList<ResultInfo> = arrayListOf()
) : Parcelable
