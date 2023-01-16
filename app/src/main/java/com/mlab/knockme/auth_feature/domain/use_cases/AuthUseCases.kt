package com.mlab.knockme.auth_feature.domain.use_cases

data class AuthUseCases(
    val isUserAuthenticated: IsUserAuthenticated,
    val firebaseAuthState: FirebaseAuthState,
    val firebaseSignIn: FirebaseSignIn,
    val facebookLogin: FacebookLogin,
    val firebaseSignOut: FirebaseSignOut,
    val getStudentIdInfo: GetStudentIdInfo,
    val getStudentInfo: GetStudentInfo

)
