package com.mlab.knockme.main_feature.presentation

import androidx.annotation.DrawableRes
import com.mlab.knockme.R
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

object MainScreens {
    @Serializable data object ChatPersonal
    @Serializable data object ChatPlaceWise
    @Serializable data object ChatBusInfo
    @Serializable object Profile {
        @Serializable data class Cgpa(val studentId: String)
        @Serializable data class CgpaInner(val studentId: String, val index: Int)
        @Serializable data object Due
        @Serializable data object RegisteredCourse
        @Serializable data object LiveResult
        @Serializable data object Clearance
    }
}

object InnerScreens{
    @Serializable data class UserProfile(val studentId: String)
    @Serializable data class Conversation(val path: String, val studentId: String)
}

@Suppress("UNCHECKED_CAST", "UnusedReceiverParameter")
@OptIn(ExperimentalSerializationApi::class)
inline val <reified T> T.route
    get() = (serializer(typeOf<T>()) as KSerializer<T>).descriptor.serialName

enum class NavItems(val route:String, val title:String, @param:DrawableRes val iconId: Int){
    Personal(MainScreens.ChatPersonal.route,"Chats", R.drawable.ic_chat),
    PlaceWise(MainScreens.ChatPlaceWise.route,"Placewise",R.drawable.ic_education),
    BusInfo(MainScreens.ChatBusInfo.route,"Bus Info",R.drawable.ic_bus),
    Profile(MainScreens.Profile.route,"Profile",R.drawable.ic_profile)
}


