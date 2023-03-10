package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.LiveResultInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class UpdateLiveResultInfo @Inject constructor(
    private val repo: MainRepo
) {
    suspend operator fun invoke(
        id: String,
        accessToken: String,
        liveResultInfoList: List<LiveResultInfo>,
        Success: (liveResultInfoList: List<LiveResultInfo>) -> Unit,
        Failed: (msg:String) -> Unit
    )= repo.updateLiveResultInfo(id, accessToken, liveResultInfoList, Success, Failed)
}