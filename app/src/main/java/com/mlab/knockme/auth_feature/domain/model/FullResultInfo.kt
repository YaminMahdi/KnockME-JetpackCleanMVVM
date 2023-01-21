package com.mlab.knockme.auth_feature.domain.model

import com.himanshoe.charty.combined.model.CombinedBarData

data class FullResultInfo(
    var semesterInfo: SemesterInfo = SemesterInfo(),
    var resultInfo: List<ResultInfo> = emptyList()
){
    fun toCombinedBarData()=
        CombinedBarData(
            semesterInfo.semesterName[0]+"-${semesterInfo.semesterYear%100}",
            semesterInfo.sgpa.toFloat(),
            semesterInfo.sgpa.toFloat()
        )
}
