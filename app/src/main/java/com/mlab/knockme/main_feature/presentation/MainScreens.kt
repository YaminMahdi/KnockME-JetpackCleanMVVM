package com.mlab.knockme.main_feature.presentation

import androidx.annotation.DrawableRes
import com.mlab.knockme.R

sealed class MainScreens(val route:String, val title:String, @DrawableRes val iconId: Int){
    data object CtPersonalScreen : MainScreens("chat_personal","Chats", R.drawable.ic_chat)
    data object CtPlacewiseScreen : MainScreens("chat_placewise","Placewise",R.drawable.ic_education)  //?fbId={fbId}&pic={pic}
    data object CtBusInfoScreen : MainScreens("chat_bus_info","Bus Info",R.drawable.ic_bus)
    data object ProScreen : MainScreens("profile","Profile",R.drawable.ic_profile)



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
