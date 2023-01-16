package com.mlab.knockme.auth_feature.domain.use_cases

import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import javax.inject.Inject

class FirebaseAuthState@Inject constructor(
    private val repo: AuthRepo
) {
    operator fun invoke() = repo.getFirebaseAuthState()
}