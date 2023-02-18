package com.mlab.knockme.auth_feature.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.domain.model.FBResponse
import com.mlab.knockme.auth_feature.presentation.login.components.LoadingScreen
import com.mlab.knockme.auth_feature.presentation.login.components.LoginPortalScreen
import com.mlab.knockme.auth_feature.presentation.login.components.LoginScreen
import com.mlab.knockme.core.util.Resource
import com.mlab.knockme.main_feature.presentation.MainActivity
import com.mlab.knockme.ui.theme.KnockMETheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val callbackManager = CallbackManager.Factory.create()
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

                val loadingState by loginViewModel.loadingState.collectAsState()
                NavHost(navController = navController,
                    startDestination =
                    if(isLoggedIn) AuthScreens.LogPortalScreen.route
                    else { AuthScreens.LogFacebookScreen.route}
                    ){
                    composable(route = AuthScreens.LogFacebookScreen.route){
                        LoginScreen{ view ->
                            val buttonFacebookLogin = view.findViewById<LoginButton>(R.id.login_button)
                            loginViewModel.signInFB(
                                buttonFacebookLogin,
                                callbackManager,
                                {data->
                                    loginViewModel.activeLoading()
                                    //navController.navigate(AuthScreens.LoadingInfoScreen.route)
                                    loginViewModel.signInFirebase(AccessToken.getCurrentAccessToken()!!, {
                                        preferencesEditor.putString("fbId", data.fbId).apply()
                                        preferencesEditor.putString("fbLink", data.fbLink).apply()
                                        preferencesEditor.putString("pic", data.pic).apply()
                                        loginViewModel.deactivateLoading()
                                        navController.navigate(AuthScreens.LogPortalScreen.route)
                                    },{})

                                },{})
                        }
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
                        val context = LocalContext.current
                        val fbInfo= FBResponse(
                            AccessToken.getCurrentAccessToken()!!,
                            sharedPreferences.getString("fbId",null)!!,
                            sharedPreferences.getString("fbLink",null)!!,
                            sharedPreferences.getString("pic",null)!!
                        )
                        LoginPortalScreen{ id, pass ->
                            var once = true
                            loginViewModel.getStudentInfo(id, pass, fbInfo)
                            lifecycleScope.launch {
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
                                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    KnockMETheme {
        LoginScreen{ view ->
            val btn = view.findViewById<LoginButton>(R.id.login_button)
            btn.setPermissions("id", "name", "link")
        }
    }
}