package com.mlab.knockme.auth_feature.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.mlab.knockme.auth_feature.domain.model.FBResponse
import com.mlab.knockme.auth_feature.domain.model.StudentInfo
import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.auth_feature.domain.use_cases.AuthUseCases
import com.mlab.knockme.auth_feature.util.SignResponse
import com.mlab.knockme.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {
    val isUserAuthenticated get() = authUseCases.isUserAuthenticated()

    private val _signInState = MutableStateFlow<SignResponse<Boolean>>(SignResponse.Success(false))
    val signInState = _signInState.asStateFlow()
    private val _signOutState = MutableStateFlow<SignResponse<Boolean>>(SignResponse.Success(false))
    val signOutState = _signOutState.asStateFlow()

    private val _authState = mutableStateOf<Boolean>(false)
    val authState : State<Boolean> = _authState

    private val _infoState = MutableStateFlow<Resource<UserProfile>>(Resource.Loading("Loading.."))
    val infoState = _infoState.asStateFlow()

    fun getStudentInfo(
        id:String,
        pass:String,
        fbInfo: FBResponse) {
        viewModelScope.launch {
            authUseCases.getStudentInfo(id, pass, fbInfo)
            .collect{
                _infoState.value=it
            }
        }
    }

    fun signInFirebase(token: AccessToken,
                       success:()->Unit,
                       failed:()->Unit) {
        authUseCases.firebaseSignIn(token,success,failed)
    }
    fun signInFB(buttonFacebookLogin: LoginButton,
                 callbackManager: CallbackManager,
                 success:(data: FBResponse)->Unit,
                 failed:()->Unit) {
        authUseCases.facebookLogin(buttonFacebookLogin,callbackManager,
            success,failed)
    }

    fun signOut() {
        viewModelScope.launch{
            authUseCases.firebaseSignOut().collect{
                _signOutState.value=it
                if(it==SignResponse.Success(true))
                    _signInState.value=SignResponse.Success(false)
            }
        }
    }

    fun getAuthState() {
        viewModelScope.launch{
            authUseCases.firebaseAuthState().collect{
                _authState.value=it
            }
        }
    }
    
}