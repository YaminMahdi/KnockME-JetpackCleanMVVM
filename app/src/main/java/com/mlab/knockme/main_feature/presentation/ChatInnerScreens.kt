package com.mlab.knockme.main_feature.presentation

sealed class ChatInnerScreens(val route:String){
    data object UserProfileScreen : ChatInnerScreens("pro_view/")  //?fbId={fbId}&pic={pic}
    data object MsgScreen : ChatInnerScreens("msg?")

}
