package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetOrCreateUserProfileInfo @Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        id: String,
        Success: (profileList: Msg) -> Unit,
        Loading: (msg: String) -> Unit,
        Failed: (msg:String) -> Unit
    )= repo.getOrCreateUserProfileInfo(id, Success, Loading, Failed)
}