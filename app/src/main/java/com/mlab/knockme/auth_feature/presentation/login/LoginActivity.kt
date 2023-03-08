package com.mlab.knockme.auth_feature.presentation.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.FBResponse
import com.mlab.knockme.auth_feature.presentation.login.components.LoadingScreen
import com.mlab.knockme.auth_feature.presentation.login.components.LoginPortalScreen
import com.mlab.knockme.auth_feature.presentation.login.components.LoginScreen
import com.mlab.knockme.core.components.InAppUpdate
import com.mlab.knockme.core.util.Resource
import com.mlab.knockme.main_feature.presentation.MainActivity
import com.mlab.knockme.ui.theme.KnockMETheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val callbackManager = CallbackManager.Factory.create()
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val preferencesEditor = sharedPreferences.edit()

//        lifecycleScope.launch{
//
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                loginViewModel.signInState.collect{
//                    when (it) {
//                        is SignResponse.Success ->{
//                            //Toast.makeText(parent, "Successful ${it.data}", Toast.LENGTH_SHORT).show()
//                            Log.d("TAG", "onCreate: Successful ${it.data}")
//                        }
//                        is SignResponse.Error -> Log.d("TAG F", it.msg)
//
//                        else -> {
//
//                        } } } } }
        setContent {
            KnockMETheme {
                // A surface container using the 'background' color from the theme
                val accessToken = AccessToken.getCurrentAccessToken()
                val isLoggedIn = accessToken != null && !accessToken.isExpired
                val navController = rememberNavController()
                val context = LocalContext.current

                val startForResult =
                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        if (it.resultCode == Activity.RESULT_OK) {
                            val intent = it.data
                            if (it.data != null) {
                                val task: Task<GoogleSignInAccount> =
                                    GoogleSignIn.getSignedInAccountFromIntent(intent)

                                preferencesEditor.putString("fbId", task.result.email!!).apply()
                                preferencesEditor.putString("fbLink", "").apply()
                                var photoUrl = task.result.photoUrl.toString()
                                photoUrl= photoUrl.replace("s96-c","s480-c")
                                Log.d("TAG", "onCreate: $photoUrl")
                                preferencesEditor.putString("pic", photoUrl).apply()
                                loginViewModel.deactivateLoading()
                                loginViewModel.signInFirebase(task.result.idToken!!, {
                                    loginViewModel.deactivateLoading()
                                    navController.navigate(AuthScreens.LogPortalScreen.route)
                                },{})
                                navController.navigate(AuthScreens.LogPortalScreen.route)
                            }

                        }else if(it.resultCode == Activity.RESULT_CANCELED){
                            loginViewModel.deactivateLoading()
                        }
                    }

                val loadingState by loginViewModel.loadingState.collectAsState()
                NavHost(navController = navController,
                    startDestination =
                    if(isLoggedIn) AuthScreens.LogPortalScreen.route
                    else { AuthScreens.LogFacebookScreen.route}
                    ){
                    composable(route = AuthScreens.LogFacebookScreen.route){
                        InAppUpdate()
                        LoginScreen({ view ->
                            val buttonFacebookLogin = view.findViewById<LoginButton>(R.id.login_button)
                            loginViewModel.signInFB(
                                buttonFacebookLogin,
                                callbackManager,
                                {data->
                                    loginViewModel.activeLoading()
                                    preferencesEditor.putString("fbId", data.fbId).apply()
                                    preferencesEditor.putString("fbLink", data.fbLink).apply()
                                    preferencesEditor.putString("pic", data.pic).apply()
                                    //navController.navigate(AuthScreens.LoadingInfoScreen.route)
                                    loginViewModel.signInFirebase(AccessToken.getCurrentAccessToken()!!, {
                                        loginViewModel.deactivateLoading()
                                        navController.navigate(AuthScreens.LogPortalScreen.route)
                                    },{})

                                },{})
                        },{view ->
                            val btnGoogleLogin = view.findViewById<SignInButton>(R.id.sign_in_button)
                            btnGoogleLogin.setOnClickListener {
                                loginViewModel.activeLoading()
                                startForResult.launch(getGoogleLoginAuth(context).signInIntent)
                            }

                        })
                        if(loadingState._isLoadingActive)
                            LoadingScreen(data = loadingState._loadingText)
                    }
                    composable(
                        route =
                        AuthScreens.LogPortalScreen.route
//                        arguments = listOf(
//                            navArgument("fbId"){ type= NavType.StringType },
//                            navArgument("fbLink"){ type= NavType.StringType },
//                            navArgument("pic"){ type= NavType.StringType }
//                        )
                    ){
                        val cont = LocalContext.current
                        val fbInfo= FBResponse(
                            AccessToken.getCurrentAccessToken(),
                            sharedPreferences.getString("fbId","")!!,
                            sharedPreferences.getString("fbLink","")!!,
                            sharedPreferences.getString("pic","")!!
                        )
                        LoginPortalScreen{ id, pass ->
                            var once = true
                            loginViewModel.getStudentInfo(id, pass, fbInfo)
                            GlobalScope.launch(Dispatchers.IO) {
                                loginViewModel.infoState.collect {
                                    when (it) {
                                        is Resource.Success -> {
                                            Log.d("TAG", "onCreate Success: ${it.message}")
                                            val intent =
                                                Intent(this@LoginActivity, MainActivity::class.java)
                                            preferencesEditor.putString("studentId", id).apply()
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
                                                Looper.prepare()
                                                Toast.makeText(cont, it.message, Toast.LENGTH_SHORT).show()
                                                Looper.loop()
                                            }
                                        }
                                    }
                                }
                            }
                            //navController.navigate(AuthScreens.LoadingInfoScreen.route+"/{$id}")
                        }
                        if(loadingState._isLoadingActive)
                            LoadingScreen(data = loadingState._loadingText)
                    }
                    composable(route = AuthScreens.LoadingInfoScreen.route){

                    }
//                    composable(route = AuthScreens.LoadingInfoScreen.route+"/{id}",
//                        arguments = listOf(navArgument("id") { type = NavType.StringType })
//                    ){
//                        LoadingScreen("")
//                    }
                }
                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
            }


        }
    }
    override fun onStart() {
        super.onStart()
        val sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        if(sharedPreferences.getString("studentId",null) != null && Firebase.auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }
    private fun getGoogleLoginAuth(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.knock_me_web_client_id))
            .requestId()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

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