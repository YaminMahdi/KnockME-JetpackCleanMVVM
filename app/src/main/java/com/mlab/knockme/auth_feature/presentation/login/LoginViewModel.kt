package com.mlab.knockme.auth_feature.presentation.login

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mlab.knockme.auth_feature.domain.model.SocialAuthInfo
import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.auth_feature.domain.use_cases.AuthUseCases
import com.mlab.knockme.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    private val _loadingText = savedStateHandle.getStateFlow("loadingText", "Loading..")
    private val _isLoadingActive = savedStateHandle.getStateFlow("isLoadingActive", false)

    val loadingState = combine(_loadingText,_isLoadingActive){_loadingText,_isLoadingActive ->
        LoadingState(_loadingText, _isLoadingActive) //1 searchNotes.execute(notes, searchText) ,
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(10) , LoadingState())

    private val _authState = mutableStateOf(false)

    private val _infoState = MutableStateFlow<Resource<UserProfile>>(Resource.Loading("Loading.."))
    val infoState = _infoState.asStateFlow()

    fun getStudentInfo(
        id: String,
        pass: String,
        socialAuthInfo: SocialAuthInfo
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            activeLoading()
            authUseCases.getStudentInfo(id, pass, socialAuthInfo)
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
        token: Any,
        success: () -> Unit,
        failed: () -> Unit
    ) {
        authUseCases.firebaseSignIn(token,
            success, failed)
    }

    fun signInFB(
        buttonFacebookLogin: LoginButton,
        callbackManager: CallbackManager,
        success: (data: SocialAuthInfo) -> Unit,
        failed: () -> Unit
    ) {
        authUseCases.facebookLogin(
            buttonFacebookLogin, callbackManager,
            success, failed
        )
    }

    fun signInGoogle(
        activity: Activity,
        success: () -> Unit,
        failed: (String) -> Unit
    ){
        activeLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when(val result= authUseCases.googleSignIn(activity)){
                is Resource.Success -> {
                    authUseCases.firebaseSignIn(result.data!!,
                        success = {
                            deactivateLoading()
                            success()
                        }, failed = { deactivateLoading()
                            viewModelScope.launch(Dispatchers.Main){
                                failed("Firebase sign in failed")
                            }
                        }
                    )
                }
                is Resource.Error -> {
                    deactivateLoading(if (result.code == 20) 0 else 700)
                    withContext(Dispatchers.Main){
                        failed(result.message ?: "Google sign in failed")
                    }
                }
                else -> Unit
            }
        }

    }

    fun activeLoading() {
        savedStateHandle["isLoadingActive"] = true
    }
    fun deactivateLoading(delay: Long =700L) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(delay)
            savedStateHandle["isLoadingActive"] = false
        }
    }

    fun signOut() {
        viewModelScope.launch{
            Firebase.auth.signOut()
        }
    }

    fun getAuthState() {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCases.firebaseAuthState().collect {
                _authState.value = it
            }
        }
    }

}