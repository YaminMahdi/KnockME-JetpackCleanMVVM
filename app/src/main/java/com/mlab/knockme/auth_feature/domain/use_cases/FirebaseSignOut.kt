package com.mlab.knockme.auth_feature.domain.use_cases

import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import javax.inject.Inject

class FirebaseSignOut@Inject constructor(
    private val repo: AuthRepo
) {
    suspend operator fun invoke() = repo.firebaseSignOut()
}