package com.mlab.knockme.auth_feature.presentation.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.SignInButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.SocialAuthInfo
import com.mlab.knockme.auth_feature.presentation.login.components.LoadingScreen
import com.mlab.knockme.auth_feature.presentation.login.components.LoginPortalScreen
import com.mlab.knockme.auth_feature.presentation.login.components.LoginScreen
import com.mlab.knockme.core.components.InAppUpdate
import com.mlab.knockme.core.util.Resource
import com.mlab.knockme.core.util.toast
import com.mlab.knockme.main_feature.presentation.MainActivity
import com.mlab.knockme.pref
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.KnockMETheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val callbackManager = CallbackManager.Factory.create()
    private val inAppUpdate = InAppUpdate(this)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplash = true
        installSplashScreen().setKeepOnScreenCondition{ keepSplash }
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        GlobalScope.launch(Dispatchers.IO){
            val id = pref.getString("studentId",null)
            if(!id.isNullOrEmpty() && Firebase.auth.currentUser != null) {
               withContext(Dispatchers.Main.immediate){
                   startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                   this@LoginActivity.finish()
               }
            }
            else {
                keepSplash = false
                lifecycleScope.launch(Dispatchers.IO) {
                    pref.edit { clear() }
                }
            }
        }
        super.onCreate(savedInstanceState)


        setContent {
            KnockMETheme {
//                val accessToken = AccessToken.getCurrentAccessToken()
//                val isLoggedIn = accessToken != null && !accessToken.isExpired
                val isLoggedIn = Firebase.auth.currentUser != null
                val navController = rememberNavController()
                val context = LocalContext.current

//                val startForResult =
//                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                        if (it.resultCode == Activity.RESULT_OK) {
//                            val intent = it.data
//                            if (it.data != null) {
//                                val task: Task<GoogleSignInAccount> =
//                                    GoogleSignIn.getSignedInAccountFromIntent(intent)
//
//                                preferencesEditor.putString("userId", task.result.email!!).apply()
//                                preferencesEditor.putString("fbLink", "").apply()
//                                var photoUrl = task.result.photoUrl.toString()
//                                photoUrl= photoUrl.replace("s96-c","s480-c")
//                                Log.d("TAG", "onCreate: $photoUrl")
//                                preferencesEditor.putString("pic", photoUrl).apply()
//                                loginViewModel.deactivateLoading()
//                                loginViewModel.signInFirebase(task.result.idToken!!, {
//                                    loginViewModel.deactivateLoading()
//                                    navController.navigate(AuthScreens.LogPortalScreen.route)
//                                },{})
//                                navController.navigate(AuthScreens.LogPortalScreen.route)
//                            }
//
//                        }else if(it.resultCode == Activity.RESULT_CANCELED){
//                            loginViewModel.deactivateLoading()
//                        }
//                    }

                val loadingState by loginViewModel.loadingState.collectAsStateWithLifecycle()
                NavHost(navController = navController,
                    modifier = Modifier
                        .background(DeepBlue)
                        .systemBarsPadding(),
                    startDestination =
                    if(isLoggedIn) AuthScreens.LoginScreenPortal
                    else { AuthScreens.LoginScreenFB}
                    ){
                    composable<AuthScreens.LoginScreenFB>{
                        inAppUpdate.checkForUpdate()
                        LoginScreen({ view ->
                            val buttonFacebookLogin = view.findViewById<LoginButton>(R.id.login_button)
                            loginViewModel.signInFB(
                                buttonFacebookLogin,
                                callbackManager,
                                { data ->
                                    loginViewModel.activeLoading()
                                    //navController.navigate(AuthScreens.LoadingInfoScreen.route)
                                    loginViewModel.signInFirebase(AccessToken.getCurrentAccessToken()!!, {
                                        loginViewModel.deactivateLoading()
                                        navController.navigate(AuthScreens.LoginScreenPortal)
                                    },{})

                                },{})
                        },{view ->
                            val btnGoogleLogin = view.findViewById<SignInButton>(R.id.sign_in_button)
                            btnGoogleLogin.setOnClickListener {
                                loginViewModel.signInGoogle(this@LoginActivity,
                                    success = {
                                        navController.navigate(AuthScreens.LoginScreenPortal)
                                    }, failed = {
                                        context.toast(it)
                                    }
                                )
                            }

                        })
                        if(loadingState.isLoadingActive)
                            LoadingScreen(data = loadingState.loadingText)
                    }
                    composable<AuthScreens.LoginScreenPortal>{
                        val context = LocalContext.current
                        val scope = rememberCoroutineScope { Dispatchers.Main }
                        LoginPortalScreen{ id, pass ->
                            var once = true
                            loginViewModel.getStudentInfo(id, pass)
                            scope.launch{
                                loginViewModel.infoState.collect {
                                    when (it) {
                                        is Resource.Success -> {
                                            Log.d("TAG", "onCreate Success: ${it.message}")
                                            val intent =
                                                Intent(this@LoginActivity, MainActivity::class.java)
                                            //intent.putExtra("id",id)
                                            startActivity(intent)
                                            finish()
                                        }

                                        is Resource.Loading -> {
                                            Log.d("TAG", "onCreate Loading: ${it.message}")
                                            //LoadingScreen(data = it.message.toString())
                                        }

                                        is Resource.Error -> {
                                            Log.d("TAG", "onCreate Error: ${it.message}")
                                            if(once){
                                                once = false
                                                context.toast(it.message)
                                            }
                                        }
                                    }
                                }
                            }
                            //navController.navigate(AuthScreens.LoadingInfoScreen.route+"/{$id}")
                        }
                        if(loadingState.isLoadingActive)
                            LoadingScreen(data = loadingState.loadingText)
                    }
                }
                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            }


        }
    }
    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }
//    private fun getGoogleLoginAuth(context: Context): GoogleSignInClient {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .requestIdToken(context.getString(R.string.knock_me_web_client_id))
//            .requestId()
//            .requestProfile()
//            .build()
//        return GoogleSignIn.getClient(context, gso)
//    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    KnockMETheme {
        LoginScreen({ view ->
            val btn = view.findViewById<LoginButton>(R.id.login_button)
            btn.setPermissions("id", "name", "link")
        },{})
    }
}