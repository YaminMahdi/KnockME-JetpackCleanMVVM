package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetOrCreateUserProfileInfo @Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        id: String,
        programId: String?,
        success: (profileList: Msg) -> Unit,
        loading: (msg: String) -> Unit,
        failed: (msg:String) -> Unit
    )= repo.getOrCreateUserProfileInfo(id, programId, success, loading, failed)
}