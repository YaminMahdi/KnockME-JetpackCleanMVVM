package com.mlab.knockme.auth_feature.domain.use_cases

import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val isUserAuthenticated: IsUserAuthenticated,
    val firebaseAuthState: FirebaseAuthState,
    val firebaseSignIn: FirebaseSignIn,
    val googleSignIn: GoogleSignIn,
    val facebookLogin: FacebookLogin,
    val getStudentIdInfo: GetStudentIdInfo,
    val getStudentInfo: GetStudentInfo

)
