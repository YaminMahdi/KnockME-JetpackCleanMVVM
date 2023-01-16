package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetChatProfiles @Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        path: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg:String) -> Unit
    )= repo.getChatProfiles(path, Success, Failed)
}