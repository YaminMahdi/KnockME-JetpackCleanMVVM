package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetUserFullProfile @Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        id: String,
        success: (userProfile: UserProfile) -> Unit,
        failed: (msg:String) -> Unit
    )= repo.getUserFullProfile(id, success, failed)
}