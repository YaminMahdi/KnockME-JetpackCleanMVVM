package com.mlab.knockme.main_feature.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.integrity.internal.u
import com.mlab.knockme.auth_feature.data.data_source.dto.DailyHadithDto
import com.mlab.knockme.auth_feature.domain.model.*
import com.mlab.knockme.main_feature.domain.model.*
import com.mlab.knockme.main_feature.domain.use_case.MainUseCases
import com.mlab.knockme.pref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainUseCases: MainUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val loadingText = savedStateHandle.getStateFlow("loadingText", "Loading..")
    private val _msg= MutableStateFlow<List<Msg>>(emptyList())
    val msg = _msg.asStateFlow()

    val msgList = savedStateHandle.getStateFlow("msgList", emptyList<Msg>())
//    private val msgText = savedStateHandle.getStateFlow("msgText", "")
//    val msgState = combine(msgList,msgText){msgList,msgText->
//        MsgListState(msgList, msgText)
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(100), MsgListState())

    //val msgList = msgListState.value

    private val chatList = savedStateHandle.getStateFlow("chatList", emptyList<Msg>())
    //val chatListState = chatList

    private val searchText = savedStateHandle.getStateFlow("searchText", "")
    private val isSearchActive = savedStateHandle.getStateFlow("isSearchActive", false)
    private val isSearchLoading = savedStateHandle.getStateFlow("isSearchLoading", false)

    val state = combine(chatList,searchText,isSearchActive,isSearchLoading,loadingText)
    {chatList,searchText,isSearchActive,isSearchLoading,loadingText ->
        ChatListState(chatList, searchText, isSearchActive,isSearchLoading,loadingText) //1 searchNotes.execute(notes, searchText) ,
    }.stateIn(viewModelScope, SharingStarted.Eagerly , ChatListState())


    private var getMsgJob: Job? =null
    private var getChatsProfileJob: Job? =null
    private var searchJob: Job? =null


    fun getMeg(
        path: String,
        failed: (msg: String) -> Unit
    ) {
        getMsgJob?.apply {
            this.cancel()
            savedStateHandle["msgList"] = emptyList<Msg>()
        }
        getMsgJob = viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.getMsg(path,{
                val x = mutableListOf<Msg>()
                x.addAll(it)
                savedStateHandle["msgList"] = x
                Log.d("TAG", "getMeg: hi from  savedStateHandle[\"msgList\"] = it")
            }, failed)
        }
    }

    fun sendMsg(
        path: String,
        msg: Msg,
        failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            //savedStateHandle["msgText"] = msg.msg
            mainUseCases.sendMsg(path, msg, failed)
        }
    }
    fun refreshProfileInChats(path: String, msg: Msg, failed: (msg: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            //savedStateHandle["msgText"] = msg.msg
            mainUseCases.refreshProfileInChats(path, msg, failed)
        }
    }

    fun getChatProfiles(
        path: String,
        failed: (msg:String) -> Unit
    ){
        getChatsProfileJob?.cancel()
        searchJob?.cancel()
        savedStateHandle["chatList"] = emptyList<Msg>()
        savedStateHandle["searchText"] = ""

        getChatsProfileJob= viewModelScope.launch(Dispatchers.IO){
            mainUseCases.getChatProfiles(
                path, {
                    val x =it.toList()
                    savedStateHandle["chatList"] = x
                }, failed)
        }
    }
    fun setSearchActive(visibility: Boolean){
        savedStateHandle["isSearchActive"] = visibility
        if(!visibility) {
            searchJob?.cancel()
            savedStateHandle["isSearchLoading"] = false
        }

    }

    fun searchUser(
        id: String
    ){
        var searchList= mutableListOf<Msg>()
        savedStateHandle["chatList"] = emptyList<Msg>()
        savedStateHandle["searchText"] = id

        if(id.isEmpty()) {
            savedStateHandle["isSearchActive"] = false
            savedStateHandle["isSearchLoading"] = false
        }
        else if(id.isNotEmpty()){
            savedStateHandle["isSearchActive"] = true
        }
        if (id.length>9){
            var userCount=10
            var done=0
            savedStateHandle["isSearchLoading"] = true
            getChatsProfileJob?.cancel()
            searchJob?.apply {
                cancel()
                searchList = mutableListOf()
            }
            searchJob=
                viewModelScope.launch(Dispatchers.IO){
                    delay(500)
                    getIdRange(id).forEachIndexed{ i,id->
                        if(i!=0)
                            delay(i*250L+700L)
                        mainUseCases.getOrCreateUserProfileInfo(
                            id, {user ->
                                Log.d("TAG69", "searchUser: $user")
                                if(user.id==id)
                                    searchList.add(0,user)
                                else
                                    searchList.add(user)
                                //searchList.sortBy { it.id }  //Array index out of range: 11
                                val x =mutableListOf<Msg>()
                                x.addAll(searchList)
                                savedStateHandle["chatList"] = x
                                done++
                                Log.d("countX", "searchUser: $done $userCount")
                                if(done>=userCount-1)
                                    lateSearchDeactivate()
                            }, {
                                savedStateHandle["loadingText"] = it
                            } ,{
                                savedStateHandle["loadingText"] = it
                                userCount--
                                if(done>=userCount-1)
                                    lateSearchDeactivate()
                                Log.d("countX", "searchUser: $done $userCount")

                            }
                        )
                    }
                }

        }
    }

    private fun lateSearchDeactivate(){
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            savedStateHandle["isSearchLoading"] = false
        }
    }
    private fun getIdRange(id: String): List<String>{
        val idRange = mutableListOf(id)
        val l =id.last()
        val last = if(l.isDigit()) l.toString().toInt() else 0
        val idWithOutLast =id.dropLast(1)
        for(i in 0..9){
            if(i!=last)
                idRange.add(idWithOutLast+i.toString())
        }
        return idRange
    }


    //profile view
    val isLoading = savedStateHandle.getStateFlow("isLoading", true)
    //val isLoading = _isLoading
    val hasPrivateInfo = savedStateHandle.getStateFlow("hasPrivateInfo", false)
    //val hasPrivateInfo = _hasPrivateInfo
    val userBasicInfo = savedStateHandle.getStateFlow("userBasicInfo", UserBasicInfo())
    val tarBasicInfo = savedStateHandle.getStateFlow("tarBasicInfo", UserBasicInfo())
    val myBasicInfo = savedStateHandle.getStateFlow("myBasicInfo", UserBasicInfo())

    //val userBasicInfo = _userBasicInfo
    //val stateProfile = ViewProfileState(isLoading.collectAsStateWithLifecycle(),hasPrivateInfo,userBasicInfo)
    //var stateProfile :StateFlow<ViewProfileState>? = null

//    fun clearUserBasicInfo(){
//        stateProfile = combine(isLoading,hasPrivateInfo,userBasicInfo)
//        {x,y,z -> ViewProfileState(x, y, z) }
//            .stateIn(viewModelScope, SharingStarted.Eagerly, ViewProfileState())
//    }
    fun getTarBasicInfo(id: String){
    viewModelScope.launch(Dispatchers.IO) {
        mainUseCases.getUserBasicInfo(id,
            {
                savedStateHandle["tarBasicInfo"] = it
            },{ })
        }
    }

    fun updateTarBasicInfo(userBasicInfo: UserBasicInfo?){
        savedStateHandle["tarBasicInfo"] = userBasicInfo
    }

    fun getMyBasicInfo(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.getUserBasicInfo(id,
                {
                    savedStateHandle["myBasicInfo"] = it
                },{ })
        }
    }

    fun getUserBasicInfo(id: String, success: (profile:UserBasicInfo) -> Unit){
    savedStateHandle["hasPrivateInfo"] = false
    savedStateHandle["isLoading"] = true

    viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.getUserBasicInfo(id,
                {
                    savedStateHandle["userBasicInfo"] = it
                    success.invoke(it)
                    viewModelScope.launch(Dispatchers.IO) {
                        delay(700)
                        savedStateHandle["isLoading"] = false
                    }
                    if(!it.privateInfo.email.isNullOrEmpty())
                        savedStateHandle["hasPrivateInfo"] = true
                },{
                    savedStateHandle["isLoading"] = false
                })
        }
    }



    val userFullProfileInfo = savedStateHandle.getStateFlow("userFullProfileInfo", UserProfile(publicInfo = PublicInfo(
        id = pref.getString("studentId","")!!,
        nm = pref.getString("nm","")!!,
        progShortName = pref.getString("proShortName","")!!,
    )))
//    val userFullResultInfo = savedStateHandle.getStateFlow("userFullResultInfo", emptyList<FullResultInfo>())
//    val userPaymentInfo = savedStateHandle.getStateFlow("userPaymentInfo", PaymentInfo())
//    val userLiveResultInfo = savedStateHandle.getStateFlow("userLiveResultInfo", emptyList<LiveResultInfo>())
//    val userRegCourseInfo = savedStateHandle.getStateFlow("userRegCourseInfo", emptyList<CourseInfo>())
    val resultLoadingTxt = savedStateHandle.getStateFlow("resultLoadingTxt", "Loading")
    val isResultLoading = savedStateHandle.getStateFlow("isResultLoading", false)


    fun getUserFullProfileInfo(
        id: String,
        success: (profile:UserProfile) -> Unit = {},
        failed: (msg:String) -> Unit,

    ) {
        mainUseCases.getUserFullProfile(id,{
            savedStateHandle["userFullProfileInfo"]=it
            success.invoke(it)
        },failed)
    }

    fun updateUserPaymentInfo(
        id: String,
        accessToken: String,
        paymentInfo: PaymentInfo,
        failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updatePaymentInfo(id,accessToken,paymentInfo,{
                val updatedProfile = userFullProfileInfo.value.copy(paymentInfo= it)
                savedStateHandle["userFullProfileInfo"]=updatedProfile
                //getUserFullProfileInfo(id,{},failed)
            },failed)
        }
    }
    fun updateUserLiveResultInfo(
        userProfile: UserProfile,
        failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updateLiveResultInfo(userProfile,{
                val updatedProfile = userFullProfileInfo.value.copy(liveResultInfo= it)
                savedStateHandle["userFullProfileInfo"]=updatedProfile
            },failed)
        }

    }
    fun updateUserRegCourseInfo(
        userProfile: UserProfile,
        failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updateRegCourseInfo(userProfile,{
                val updatedProfile = userFullProfileInfo.value.copy(regCourseInfo= it)
                savedStateHandle["userFullProfileInfo"]=updatedProfile
            },failed)
        }

    }

    fun updateClearanceInfo(
        id: String,
        accessToken: String,
        clearanceInfoList: List<ClearanceInfo>,
        failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updateClearanceInfo(id,accessToken,clearanceInfoList,{
                val updatedProfile = userFullProfileInfo.value.copy(clearanceInfo = it)
                savedStateHandle["userFullProfileInfo"]=updatedProfile
            },failed)
        }

    }
    fun updateUserFullResultInfo(
        publicInfo: PublicInfo,
        fullResultInfoList: List<FullResultInfo>,
    ){
        savedStateHandle["isResultLoading"]=true
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updateFullResultInfo(publicInfo,fullResultInfoList,{it,cgpa,totalCompletedCredit->

                val updatedProfile = userFullProfileInfo.value.copy(
                    fullResultInfo= ArrayList(it), publicInfo = userFullProfileInfo.value.publicInfo
                        .copy(cgpa = cgpa, totalCompletedCredit = totalCompletedCredit)
                )
                savedStateHandle["userFullProfileInfo"] = updatedProfile

                val updatedBasicInfo = userBasicInfo.value.copy(
                    fullResultInfo= ArrayList(it), publicInfo = userBasicInfo.value.publicInfo.copy(cgpa = cgpa)
                )
                savedStateHandle["userBasicInfo"] = updatedBasicInfo

                savedStateHandle["resultLoadingTxt"]="New Data Backed Up"
                delayDeactivateResultLoading()
            },{
                savedStateHandle["resultLoadingTxt"]=it
            },{
                savedStateHandle["resultLoadingTxt"]=it
                delayDeactivateResultLoading()
            })
        }

    }
    private fun delayDeactivateResultLoading(){
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            savedStateHandle["isResultLoading"] = false
        }
    }

    val hadith = savedStateHandle.getStateFlow("hadith", DailyHadithDto())
    val showHadith = savedStateHandle.getStateFlow("showHadith", true)
    val dialogVisibility = savedStateHandle.getStateFlow("dialogVisibility", false)
    val infoDialogVisibility = savedStateHandle.getStateFlow("infoDialogVisibility", false)
    fun getRandomHadith(failed: (msg:String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.getRandomHadith({
                savedStateHandle["hadith"] = it
                setDialogVisibility(true)
            },failed)
        }
    }
    fun setShowHadith(visibility: Boolean){
        savedStateHandle["showHadith"] = visibility
    }
    fun setDialogVisibility(visibility: Boolean){
        savedStateHandle["dialogVisibility"] = visibility
    }
    fun setInfoDialogVisibility(visibility: Boolean){
        savedStateHandle["infoDialogVisibility"] = visibility
    }


}