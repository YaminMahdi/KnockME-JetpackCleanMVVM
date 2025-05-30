package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class GetMsg @Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        path: String,
        success: (msgList: List<Msg>) -> Unit,
        failed: (msg:String) -> Unit
    )= repo.getMessages(path,success,failed)
}