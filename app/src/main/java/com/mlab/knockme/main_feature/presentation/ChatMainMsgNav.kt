package com.mlab.knockme.main_feature.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mlab.knockme.main_feature.presentation.chats.ChatBusInfoScreen
import com.mlab.knockme.main_feature.presentation.chats.ChatPersonalScreen
import com.mlab.knockme.main_feature.presentation.chats.ChatPlacewiseScreen
import com.mlab.knockme.main_feature.presentation.main.ProfileViewScreen

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