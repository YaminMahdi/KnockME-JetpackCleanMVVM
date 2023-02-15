package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.FullResultInfo
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class UpdateFullResultInfo @Inject constructor(
    private val repo: MainRepo
) {
    suspend operator fun invoke(
        publicInfo: PublicInfo,
        fullResultInfoList: List<FullResultInfo>,
        Success: (fullResultInfoList: List<FullResultInfo>, cgpa: Double) -> Unit,
        Loading: (msg:String) -> Unit,
        Failed: (msg:String) -> Unit
    )= repo.updateFullResultInfo(publicInfo, fullResultInfoList, Success, Loading, Failed)
}