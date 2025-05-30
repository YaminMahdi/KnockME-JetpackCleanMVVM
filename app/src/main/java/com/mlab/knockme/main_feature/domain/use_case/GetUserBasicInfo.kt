package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetUserBasicInfo @Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        id: String,
        success: (userBasicInfo: UserBasicInfo) -> Unit,
        failed: (msg:String) -> Unit
    )= repo.getUserBasicInfo(id, success, failed)
}