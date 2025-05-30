package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.ClearanceInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class UpdateClearanceInfo @Inject constructor(
    private val repo: MainRepo
) {
    suspend operator fun invoke(
        id: String,
        accessToken: String,
        clearanceInfoList: List<ClearanceInfo>,
        success: (clearanceInfoList: List<ClearanceInfo>) -> Unit,
        failed: (msg:String) -> Unit
    )= repo.updateClearanceInfo(id, accessToken, clearanceInfoList, success, failed)
}