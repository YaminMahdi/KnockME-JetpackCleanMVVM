package com.mlab.knockme.auth_feature.domain.repo

import android.app.Activity
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.mlab.knockme.auth_feature.domain.model.SocialAuthInfo
import com.mlab.knockme.auth_feature.domain.model.StudentInfo
import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepo {
    fun isUserAuthenticatedInFirebase(): Boolean

    fun getFirebaseAuthState() : Flow<Boolean>

    fun firebaseSignIn(credential: Any, success:()->Unit, failed:()->Unit)

    suspend fun googleSignIn(activity: Activity): Resource<GoogleIdTokenCredential>

    fun fbLogin(buttonFacebookLogin: LoginButton,
                callbackManager: CallbackManager,
                success:(data: SocialAuthInfo)->Unit,
                failed:()->Unit
    )

    fun getStudentIdInfo(id: String) :Flow<Resource<StudentInfo>>

    fun getStudentInfo(id:String,pass:String,socialAuthInfo: SocialAuthInfo) :Flow<Resource<UserProfile>>

}