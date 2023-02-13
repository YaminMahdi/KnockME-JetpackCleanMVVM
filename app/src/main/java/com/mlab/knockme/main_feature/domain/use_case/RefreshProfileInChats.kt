package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject
import kotlin.jvm.Throws

class RefreshProfileInChats@Inject constructor(
    private val repo: MainRepo
) {
    operator fun invoke(
        path: String,
        msg: Msg,
        Failed: (msg:String) -> Unit
    ){
        repo.refreshProfileInChats(path,msg,Failed)
    }
}