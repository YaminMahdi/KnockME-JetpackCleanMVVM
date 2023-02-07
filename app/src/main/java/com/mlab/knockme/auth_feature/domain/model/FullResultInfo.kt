package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcel
import android.os.Parcelable
import com.himanshoe.charty.combined.model.CombinedBarData
import kotlinx.parcelize.Parcelize
import java.util.ArrayList
@Parcelize
data class FullResultInfo(
    var semesterInfo: SemesterInfo = SemesterInfo(),
    var resultInfo: ArrayList<ResultInfo> = arrayListOf()
) : Parcelable {
    fun toCombinedBarData()=
        CombinedBarData(
            semesterInfo.semesterName!![0]+"-${semesterInfo.semesterYear%100}",
            semesterInfo.sgpa.toFloat(),
            semesterInfo.sgpa.toFloat()
        )
}
