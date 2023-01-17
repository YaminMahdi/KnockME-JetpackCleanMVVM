package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetUserProfileInfo @Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        id: String,
        Success: (userProfile: UserProfile) -> Unit,
        Failed: (msg:String) -> Unit
    )= repo.getUserProfileInfo(id, Success, Failed)
}