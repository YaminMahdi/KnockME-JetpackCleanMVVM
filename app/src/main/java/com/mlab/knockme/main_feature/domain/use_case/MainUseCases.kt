package com.mlab.knockme.main_feature.domain.use_case

data class MainUseCases (
    val getChatProfiles: GetChatProfiles,
    val getUserBasicInfo: GetUserBasicInfo,
    val getUserFullProfile: GetUserFullProfile,
    val getOrCreateUserProfileInfo: GetOrCreateUserProfileInfo,

    val getMsg: GetMsg,
    val sendMsg: SendMsg,
    val deleteMsg: DeleteMsg,

    val updatePaymentInfo: UpdatePaymentInfo,
    val updateRegCourseInfo: UpdateRegCourseInfo,
    val updateLiveResultInfo: UpdateLiveResultInfo,
    val updateFullResultInfo: UpdateFullResultInfo
)