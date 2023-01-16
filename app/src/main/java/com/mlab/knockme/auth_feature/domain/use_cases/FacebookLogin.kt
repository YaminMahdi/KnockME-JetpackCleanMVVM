package com.mlab.knockme.auth_feature.domain.use_cases

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.mlab.knockme.auth_feature.domain.model.FBResponse
import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import javax.inject.Inject

class FacebookLogin @Inject constructor(
    private val repo: AuthRepo
) {
    operator fun invoke(
        buttonFacebookLogin: LoginButton,
        callbackManager: CallbackManager,
        success:(data: FBResponse)->Unit,
        failed:()->Unit
    ) = repo.fbLogin(buttonFacebookLogin,callbackManager,success,failed)
}