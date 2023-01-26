package com.mlab.knockme.main_feature.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mlab.knockme.main_feature.presentation.chats.components.ChatBusInfoScreen
import com.mlab.knockme.main_feature.presentation.chats.components.ChatPersonalScreen
import com.mlab.knockme.main_feature.presentation.chats.components.ChatPlacewiseScreen
import com.mlab.knockme.main_feature.presentation.main.components.ProfileViewScreen

@Composable
fun ChatMainMsgNav ( chatScreen: Int, navController: NavHostController){  //        Failed: (msg: String) -> Unit
    //val navController = rememberNavController()
    NavHost(navController = navController,
    startDestination = ChatInnerScreens.ChatScreen.route
    ){
        composable(
            route =
            ChatInnerScreens.ChatScreen.route ,
        ){
            when(chatScreen)
            {
                1 -> ChatPersonalScreen(navController)
                2 -> ChatPlacewiseScreen(navController)
                3 -> ChatBusInfoScreen(navController)
            }
        }
        composable(route = ChatInnerScreens.UserProfileScreen.route+"{id}"
//            arguments = listOf(
//                navArgument("id"){ type= NavType.StringType })
        ){
            // ProfileViewScreen(navController, it.arguments?.getString("id"))
            ProfileViewScreen(it.arguments?.getString("id")!!,navController)
        }
    }
}