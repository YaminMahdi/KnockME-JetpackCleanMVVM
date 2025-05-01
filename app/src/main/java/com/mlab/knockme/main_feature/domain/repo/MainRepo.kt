package com.mlab.knockme.main_feature.domain.repo

import com.mlab.knockme.auth_feature.data.data_source.dto.DailyHadithDto
import com.mlab.knockme.auth_feature.domain.model.*
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo

interface MainRepo {
    fun getMessages(
        path: String,
        success: (msgList: List<Msg>) -> Unit,
        failed: (msg:String) -> Unit
    )
    fun sendMessages(path: String, msg: Msg,failed: (msg:String) -> Unit)

    fun refreshProfileInChats(path: String, msg: Msg,failed: (msg:String) -> Unit)

    fun deleteMessage(path: String, id: String): Boolean
    fun getChatProfiles(
        path: String,
        success: (profileList: List<Msg>) -> Unit,
        failed: (msg:String) -> Unit
    )

    fun getUserBasicInfo(
        id: String,
        success: (userBasicInfo: UserBasicInfo) -> Unit,
        failed: (msg:String) -> Unit
    )

    fun getUserFullProfile(
        id: String,
        success: (UserProfile) -> Unit,
        failed: (msg:String) -> Unit
    )

    fun getOrCreateUserProfileInfo(
        id: String,
        success: (Msg) -> Unit,
        loading: (msg: String) -> Unit,
        failed: (msg:String) -> Unit
    )



    suspend fun updatePaymentInfo(
        id: String,
        accessToken: String,
        paymentInfo: PaymentInfo,
        success: (PaymentInfo) -> Unit,
        failed: (msg:String) -> Unit
    )
    suspend fun updateRegCourseInfo(
        userProfile: UserProfile,
        success: (List<CourseInfo>) -> Unit,
        failed: (msg:String) -> Unit
    )

    suspend fun updateLiveResultInfo(
        userProfile: UserProfile,
        success: (List<LiveResultInfo>) -> Unit,
        failed: (msg:String) -> Unit
    )

    suspend fun updateFullResultInfo(
        publicInfo: PublicInfo,
        fullResultInfoList: List<FullResultInfo>,
        success: (List<FullResultInfo>,Double,Double) -> Unit,
        loading: (msg: String) -> Unit,
        failed: (msg:String) -> Unit
    )

    suspend fun updateClearanceInfo(
        id: String,
        accessToken: String,
        clearanceInfoList: List<ClearanceInfo>,
        success: (clearanceInfoList: List<ClearanceInfo>) -> Unit,
        failed: (msg:String) -> Unit
    )

    suspend fun getRandomHadith(
        success: (DailyHadithDto) -> Unit,
        failed: (msg:String) -> Unit
    )

}