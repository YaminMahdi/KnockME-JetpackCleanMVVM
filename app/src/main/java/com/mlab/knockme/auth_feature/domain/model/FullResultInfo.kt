package com.mlab.knockme.auth_feature.domain.model

data class FullResultInfo(
    var semesterInfo: SemesterInfo = SemesterInfo(),
    var resultInfo: List<ResultInfo> = emptyList()
)
