package com.mlab.knockme.auth_feature.domain.use_cases

import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import javax.inject.Inject

class FirebaseSignIn @Inject constructor(
    private val repo: AuthRepo
) {
    operator fun invoke(
        token: Any,
        success:()->Unit,
        failed:()->Unit
    ) = repo.firebaseSignIn(token,success,failed)
}