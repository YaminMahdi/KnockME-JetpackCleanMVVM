package com.mlab.knockme.auth_feature.domain.use_cases

import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.mlab.knockme.auth_feature.domain.model.FBResponse
import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStudentIdInfo @Inject constructor(
    private val repo: AuthRepo
) {
    operator fun invoke(
        id : String
    ) =
        if (id.isBlank())
            flow{}
        else
            repo.getStudentIdInfo(id)
}