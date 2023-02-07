package com.mlab.knockme.main_feature.presentation

sealed class ChatInnerScreens(val route:String){
    object ChatScreen : ChatInnerScreens("chat")
    object UserProfileScreen : ChatInnerScreens("pro_view/")  //?fbId={fbId}&pic={pic}

    object MsgScreen : ChatInnerScreens("msg?")

}
