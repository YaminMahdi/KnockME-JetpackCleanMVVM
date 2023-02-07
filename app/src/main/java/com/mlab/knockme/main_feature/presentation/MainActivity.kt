package com.mlab.knockme.main_feature.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.mlab.knockme.main_feature.presentation.chats.ChatBusInfoScreen
import com.mlab.knockme.main_feature.presentation.chats.ChatPersonalScreen
import com.mlab.knockme.main_feature.presentation.chats.ChatPlacewiseScreen
import com.mlab.knockme.main_feature.presentation.main.components.BottomMenuItem
import com.mlab.knockme.main_feature.presentation.main.components.BottomNav
import com.mlab.knockme.main_feature.presentation.main.ProfileViewScreen
import com.mlab.knockme.main_feature.presentation.messages.MsgViewScreen
import com.mlab.knockme.main_feature.presentation.profile.ProfileScreen
import com.mlab.knockme.ui.theme.KnockMETheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()

        setContent {
            KnockMETheme {
                // A surface container using the 'background' color from the theme
                Main(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun Main(viewModel: MainViewModel) {
    //val viewModel: MainViewModel = hiltViewModel()
    val navController = rememberAnimatedNavController()
    val navItems = listOf(
        MainScreens.CtPersonalScreen,
        MainScreens.CtPlacewiseScreen,
        MainScreens.CtBusInfoScreen,
        MainScreens.ProScreen,
    )

    Scaffold(
        bottomBar = {
            //val navVisibility = viewModel.isNavVisible.collectAsState().value
           // var bottomBarState by remember { mutableStateOf(navVisibility) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val navVisibility = navItems.any { it.route== currentDestination?.route }
            AnimatedVisibility(
                visible = navVisibility,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ){
                BottomNav{
                    navItems.forEach { screenMenuItem ->
                        BottomMenuItem(
                            item = screenMenuItem,
                            isSelected = currentDestination?.hierarchy?.any { it.route == screenMenuItem.route } == true,
                            onItemClick = {
                                navController.navigate(screenMenuItem.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
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
    ) { bottomPadding ->
        AnimatedNavHost(navController, startDestination = MainScreens.CtPersonalScreen.route, Modifier.padding(bottomPadding)) {
            composable(MainScreens.CtPersonalScreen.route) {
                //ChatMainMsgNav(1, navController)
                ChatPersonalScreen(navController,viewModel)
            }
            composable(MainScreens.CtPlacewiseScreen.route) {
                //ChatMainMsgNav(2, navController)
                ChatPlacewiseScreen(navController,viewModel)
            }
            composable(MainScreens.CtBusInfoScreen.route) {
                //ChatMainMsgNav(3, navController)
                ChatBusInfoScreen(navController,viewModel)
            }
            composable(MainScreens.ProScreen.route) {
                ProfileScreen()
            }
            composable(ChatInnerScreens.UserProfileScreen.route+"{id}"){
                // ProfileViewScreen(navController, it.arguments?.getString("id"))
                ProfileViewScreen(it.arguments?.getString("id")!!,navController,viewModel)
            }
            composable(
                ChatInnerScreens.MsgScreen.route+"path={path}&id={id}",
                enterTransition = {
                    fadeIn() + slideInVertically(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut() + slideOutVertically(animationSpec = tween(1000))
                }
            ){
                MsgViewScreen(
                    it.arguments?.getString("path")!!,
                    it.arguments?.getString("id")!!,
                    navController
                )
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


