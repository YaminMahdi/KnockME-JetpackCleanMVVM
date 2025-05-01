package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class SendMsg@Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        path: String,
        msg: Msg,
        Failed: (msg:String) -> Unit
    ){
        repo.sendMessages(path,msg,Failed)
    }
}