package com.mlab.knockme.main_feature.domain.repo

import com.mlab.knockme.auth_feature.data.data_source.dto.DailyHadithDto
import com.mlab.knockme.auth_feature.domain.model.*
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo

interface MainRepo {
    fun getMessages(
        path: String,
        Success: (msgList: List<Msg>) -> Unit,
        Failed: (msg:String) -> Unit
    )
    fun sendMessages(path: String, msg: Msg,Failed: (msg:String) -> Unit)

    fun refreshProfileInChats(path: String, msg: Msg,Failed: (msg:String) -> Unit)

    fun deleteMessage(path: String, id: String): Boolean
    fun getChatProfiles(
        path: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg:String) -> Unit
    )

    fun getUserBasicInfo(
        id: String,
        Success: (userBasicInfo: UserBasicInfo) -> Unit,
        Failed: (msg:String) -> Unit
    )

    fun getUserFullProfile(
        id: String,
        Success: (UserProfile) -> Unit,
        Failed: (msg:String) -> Unit
    )

    fun getOrCreateUserProfileInfo(
        id: String,
        Success: (Msg) -> Unit,
        Loading: (msg: String) -> Unit,
        Failed: (msg:String) -> Unit
    )



    suspend fun updatePaymentInfo(
        id: String,
        accessToken: String,
        paymentInfo: PaymentInfo,
        Success: (PaymentInfo) -> Unit,
        Failed: (msg:String) -> Unit
    )
    suspend fun updateRegCourseInfo(
        id: String,
        accessToken: String,
        regCourseInfoList: List<CourseInfo>,
        Success: (List<CourseInfo>) -> Unit,
        Failed: (msg:String) -> Unit
    )

    suspend fun updateLiveResultInfo(
        id: String,
        accessToken: String,
        liveResultInfoList: List<LiveResultInfo>,
        Success: (List<LiveResultInfo>) -> Unit,
        Failed: (msg:String) -> Unit
    )

    suspend fun updateFullResultInfo(
        publicInfo: PublicInfo,
        fullResultInfoList: List<FullResultInfo>,
        Success: (List<FullResultInfo>,Double,Double) -> Unit,
        Loading: (msg: String) -> Unit,
        Failed: (msg:String) -> Unit
    )

    suspend fun updateClearanceInfo(
        id: String,
        accessToken: String,
        clearanceInfoList: List<ClearanceInfo>,
        Success: (clearanceInfoList: List<ClearanceInfo>) -> Unit,
        Failed: (msg:String) -> Unit
    )

    suspend fun getRandomHadith(
        Success: (DailyHadithDto) -> Unit,
        Failed: (msg:String) -> Unit
    )

}