package com.mlab.knockme.auth_feature.domain.repo

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.mlab.knockme.auth_feature.domain.model.FBResponse
import com.mlab.knockme.auth_feature.domain.model.StudentInfo
import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.auth_feature.util.SignResponse
import com.mlab.knockme.core.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepo {
    fun isUserAuthenticatedInFirebase(): Boolean

    fun getFirebaseAuthState() : Flow<Boolean>

    fun firebaseSignIn(token: AccessToken,
                               success:()->Unit,
                               failed:()->Unit
    )

    suspend fun firebaseSignOut()  : StateFlow<SignResponse<Boolean>>

    fun fbLogin(buttonFacebookLogin: LoginButton,
                callbackManager: CallbackManager,
                success:(data: FBResponse)->Unit,
                failed:()->Unit
    )

    fun getStudentIdInfo(id: String) :Flow<Resource<StudentInfo>>

    fun getStudentInfo(id:String,pass:String,fbInfo: FBResponse) :Flow<Resource<UserProfile>>

}