package com.mlab.knockme.auth_feature.presentation.login

sealed class AuthScreens(val route:String){
    object LogFacebookScreen : AuthScreens("login_fb")
    object LogPortalScreen : AuthScreens("login_sp")  //?fbId={fbId}&pic={pic}

    fun withArgs(args: Map<String,String>):String =
        buildString {
            append(route)
            args.onEachIndexed { index, entry ->
                if(index==0)
                    append("?${entry.key}=${entry.value}")
                else
                    append("&${entry.key}=${entry.value}")
            }
        }
}
