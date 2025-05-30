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
        success: (fullResultInfoList: List<FullResultInfo>, cgpa: Double, totalCompletedCredit: Double) -> Unit,
        loading: (msg:String) -> Unit,
        failed: (msg:String) -> Unit
    )= repo.updateFullResultInfo(publicInfo, fullResultInfoList, success, loading, failed)
}