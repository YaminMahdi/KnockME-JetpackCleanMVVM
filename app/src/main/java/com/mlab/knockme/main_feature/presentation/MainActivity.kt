package com.mlab.knockme.main_feature.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.presentation.login.LoginActivity
import com.mlab.knockme.core.components.InAppUpdate
import com.mlab.knockme.main_feature.presentation.chats.ChatBusInfoScreen
import com.mlab.knockme.main_feature.presentation.chats.ChatPersonalScreen
import com.mlab.knockme.main_feature.presentation.chats.ChatPlacewiseScreen
import com.mlab.knockme.main_feature.presentation.main.ProfileViewScreen
import com.mlab.knockme.main_feature.presentation.main.components.BottomMenuItem
import com.mlab.knockme.main_feature.presentation.main.components.BottomNav
import com.mlab.knockme.main_feature.presentation.messages.MsgViewScreen
import com.mlab.knockme.main_feature.presentation.profile.*
import com.mlab.knockme.pref
import com.mlab.knockme.ui.theme.DeepBlue
import com.mlab.knockme.ui.theme.KnockMETheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val inAppUpdate = InAppUpdate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()

        setContent {
            KnockMETheme {
                // A surface container using the 'background' color from the theme
                Main(viewModel, inAppUpdate)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        if (Firebase.auth.currentUser == null) {
            pref.edit { clear() }
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }
}

@Composable
fun Main(viewModel: MainViewModel, inAppUpdate: InAppUpdate? = null) {
    //val viewModel: MainViewModel = hiltViewModel()
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navVisibility = NavItems.entries.any { it.route == currentDestination?.route }

    Scaffold(
        modifier = Modifier
            .background(DeepBlue)
            .systemBarsPadding(),
        bottomBar = {
            AnimatedVisibility(
                visible = navVisibility,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomNav {
                    NavItems.entries.forEach { screenMenuItem ->
                        BottomMenuItem(
                            item = screenMenuItem,
                            isSelected = currentDestination?.hierarchy?.any { it.route == screenMenuItem.route } == true,
                            onItemClick = {
                                navController.navigate(screenMenuItem.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(MainScreens.ChatPersonal) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // re selecting the same item
                                    launchSingleTop = true
                                    // Restore state when re selecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }

        }
    ) { bottomNavPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreens.ChatPersonal.route,
            modifier = Modifier.padding(bottomNavPadding)
        ) {
            composable<MainScreens.ChatPersonal> {
                //ChatMainMsgNav(1, navController)
                inAppUpdate?.checkForUpdate()
                ChatPersonalScreen(navController, viewModel)
            }
            composable<MainScreens.ChatPlaceWise> {
                //ChatMainMsgNav(2, navController)
                ChatPlacewiseScreen(navController ,viewModel)
            }
            composable<MainScreens.ChatBusInfo> {
                //ChatMainMsgNav(3, navController)
                ChatBusInfoScreen(navController ,viewModel)
            }
            composable<MainScreens.Profile> {
                ProfileScreen(navController ,viewModel)
            }
            composable<InnerScreens.UserProfile>(
//                InnerScreens.UserProfileScreen.route+"{id}",
                enterTransition = {
                    fadeIn() + slideInVertically(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut() + slideOutVertically(animationSpec = tween(1000))
                }) {
                // ProfileViewScreen(navController, it.arguments?.getString("id"))
                ProfileViewScreen(
                    id = it.toRoute<InnerScreens.UserProfile>().studentId,
                    navController = navController,
                )
            }
            composable<InnerScreens.Conversation>(
//                InnerScreens.MsgScreen.route+"path={path}&id={id}",
                enterTransition = {
                    fadeIn() + slideInVertically(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut() + slideOutVertically(animationSpec = tween(1000))
                }
            ) {
                val data = it.toRoute<InnerScreens.Conversation>()
                MsgViewScreen(
                    path = data.path,
                    id = data.studentId,
                    navController = navController,
                    viewModel = viewModel
                )
            }
            composable<MainScreens.Profile.Cgpa>(
//                MainScreens.Profile.CgpaScreen.route+"{id}",
                enterTransition = {
                    fadeIn() + slideInVertically(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut() + slideOutVertically(animationSpec = tween(1000))
                }
            ) {
                CgpaViewScreen(it.toRoute<MainScreens.Profile.Cgpa>().studentId, navController)
            }
            composable<MainScreens.Profile.CgpaInner>
//                MainScreens.Profile.CgpaInnerScreen.route+"{id}/{index}",
//                arguments = listOf(navArgument("index") { type = NavType.IntType })
            {
                it.toRoute<MainScreens.Profile.CgpaInner>().also { data ->
                    CgpaDetailsScreen(data.studentId, data.index, navController)
                }
            }
            composable<MainScreens.Profile.Due>(
//                MainScreens.Profile.DueScreen.route,
                enterTransition = {
                    fadeIn() + slideInVertically(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut() + slideOutVertically(animationSpec = tween(1000))
                }
            ) {
                DueViewScreen(navController ,viewModel)
            }
            composable<MainScreens.Profile.RegisterdCourse>(
//                MainScreens.Profile.RegCourseScreen.route,
                enterTransition = {
                    fadeIn() + slideInVertically(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut() + slideOutVertically(animationSpec = tween(1000))
                }) {
                RegCourseViewScreen(navController ,viewModel)
            }
            composable<MainScreens.Profile.LiveResult>(
//                MainScreens.Profile.LiveResultScreen.route,
                enterTransition = {
                    fadeIn() + slideInVertically(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut() + slideOutVertically(animationSpec = tween(1000))
                }) {
                LiveResultViewScreen(navController ,viewModel)
            }
            composable<MainScreens.Profile.Clearance>(
//                MainScreens.Profile.ClearanceScreen.route,
                enterTransition = {
                    fadeIn() + slideInVertically(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut() + slideOutVertically(animationSpec = tween(1000))
                }) {
                ClearanceViewScreen(navController ,viewModel)
            }
        }
    }
//    BackHandler {
//        // your action
//    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KnockMETheme {
        Main(hiltViewModel())
    }
}


