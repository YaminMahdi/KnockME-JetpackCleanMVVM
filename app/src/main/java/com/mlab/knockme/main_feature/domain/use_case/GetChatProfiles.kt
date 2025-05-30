package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetChatProfiles @Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        path: String,
        success: (profileList: List<Msg>) -> Unit,
        failed: (msg:String) -> Unit
    )= repo.getChatProfiles(path, success, failed)
}