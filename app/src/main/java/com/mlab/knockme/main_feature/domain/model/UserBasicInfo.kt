package com.mlab.knockme.main_feature.domain.model

import android.os.Parcelable
import com.himanshoe.charty.bar.model.BarData
import com.mlab.knockme.auth_feature.domain.model.FullResultInfo
import com.mlab.knockme.auth_feature.domain.model.PrivateInfoExtended
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserBasicInfo(
    val lastUpdatedResultInfo: Long=0,
    val publicInfo: PublicInfo = PublicInfo(),
    val privateInfo: PrivateInfoExtended = PrivateInfoExtended(),
    var fullResultInfo: List<FullResultInfo> = emptyList()
) : Parcelable{
    fun toBarData() = fullResultInfo.map {
        BarData(
            yValue = it.semesterInfo.sgpa.toFloat(),
            xValue = "${it.semesterInfo.semesterName!!.firstOrNull()}-${it.semesterInfo.semesterYear%100}"
        )
    }

}
