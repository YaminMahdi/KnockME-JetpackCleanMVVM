package com.mlab.knockme.auth_feature.domain.use_cases

import android.app.Activity
import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import javax.inject.Inject

class GoogleSignIn @Inject constructor(
    private val repo: AuthRepo
) {
    suspend operator fun invoke(activity: Activity) = repo.googleSignIn(activity)
}