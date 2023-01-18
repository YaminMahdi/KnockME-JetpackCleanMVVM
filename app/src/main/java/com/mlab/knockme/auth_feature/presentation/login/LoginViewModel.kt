package com.mlab.knockme.auth_feature.presentation.login

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
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
import com.mlab.knockme.main_feature.presentation.MainActivity
import com.mlab.knockme.main_feature.presentation.chats.components.ChatListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    private val _loadingText = savedStateHandle.getStateFlow("loadingText", "Loading..")
    val loadingText = _loadingText
    private val _isLoadingActive = savedStateHandle.getStateFlow("isLoadingActive", false)
    val isLoadingActive = _isLoadingActive

    val loadingState = combine(_loadingText,_isLoadingActive){_loadingText,_isLoadingActive ->
        LoadingState(_loadingText, _isLoadingActive) //1 searchNotes.execute(notes, searchText) ,
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(10) , LoadingState())
    val isUserAuthenticated get() = authUseCases.isUserAuthenticated()

    private val _signInState = MutableStateFlow<SignResponse<Boolean>>(SignResponse.Success(false))
    val signInState = _signInState.asStateFlow()
    private val _signOutState = MutableStateFlow<SignResponse<Boolean>>(SignResponse.Success(false))
    val signOutState = _signOutState.asStateFlow()

    private val _authState = mutableStateOf<Boolean>(false)
    val authState: State<Boolean> = _authState

    private val _infoState = MutableStateFlow<Resource<UserProfile>>(Resource.Loading("Loading.."))
    val infoState = _infoState.asStateFlow()

    fun getStudentInfo(
        id: String,
        pass: String,
        fbInfo: FBResponse
    ) {
        viewModelScope.launch {
            savedStateHandle["isLoadingActive"] = true
            authUseCases.getStudentInfo(id, pass, fbInfo)
                .collect {
                    _infoState.emit(it)
                    //savedStateHandle["loadingText"] = it.message
                    when(it){
                        is Resource.Success ->{
                            Log.d("TAG", "onCreate Success: ${it.data}")
                            deactivateLoading(1000)
                        }
                        is Resource.Loading ->{
                            Log.d("TAG", "onCreate Loading: ${it.message}")
                            savedStateHandle["loadingText"] = it.message
                        }
                        is Resource.Error ->{
                            Log.d("TAG", "onCreate Error: ${it.message}")
                            deactivateLoading()

                        }
                    }
                }
        }
    }
    fun signInFirebase(
        token: AccessToken,
        success: () -> Unit,
        failed: () -> Unit
    ) {
        authUseCases.firebaseSignIn(token,
            success, failed)
    }

    fun signInFB(
        buttonFacebookLogin: LoginButton,
        callbackManager: CallbackManager,
        success: (data: FBResponse) -> Unit,
        failed: () -> Unit
    ) {
        authUseCases.facebookLogin(
            buttonFacebookLogin, callbackManager,
            success, failed
        )
    }

    fun activeLoading() {
        savedStateHandle["isLoadingActive"] = true
    }
    fun deactivateLoading(delay: Long =700L) {
        viewModelScope.launch {
            delay(delay)
            savedStateHandle["isLoadingActive"] = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authUseCases.firebaseSignOut().collect {
                _signOutState.value = it
                if (it == SignResponse.Success(true))
                    _signInState.value = SignResponse.Success(false)
            }
        }
    }

    fun getAuthState() {
        viewModelScope.launch {
            authUseCases.firebaseAuthState().collect {
                _authState.value = it
            }
        }
    }

}