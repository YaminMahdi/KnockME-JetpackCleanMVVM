package com.mlab.knockme.main_feature.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlab.knockme.auth_feature.data.data_source.dto.DailyHadithDto
import com.mlab.knockme.auth_feature.domain.model.*
import com.mlab.knockme.main_feature.domain.model.*
import com.mlab.knockme.main_feature.domain.use_case.MainUseCases
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


    private val _loadingText = savedStateHandle.getStateFlow("loadingText", "Loading..")
    val loadingText = _loadingText

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
    private var getChatsJob: Job? =null
    private var searchJob: Job? =null


    fun getMeg(
        path: String,
        Failed: (msg: String) -> Unit
    ) {
        getMsgJob?.cancel()
        //savedStateHandle["msgList"] = emptyList<Msg>()
        getMsgJob = viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.getMsg(path,{
                val x = mutableListOf<Msg>()
                x.addAll(it)
                savedStateHandle["msgList"] = x
                Log.d("TAG", "getMeg: hi from  savedStateHandle[\"msgList\"] = it")
            }, Failed)
        }
    }

    fun sendMsg(
        path: String,
        msg: Msg,
        Failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            //savedStateHandle["msgText"] = msg.msg
            mainUseCases.sendMsg(path, msg, Failed)
        }
    }
    fun refreshProfileInChats(path: String, msg: Msg, Failed: (msg: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            //savedStateHandle["msgText"] = msg.msg
            if(!isSearchActive.value)
                mainUseCases.refreshProfileInChats(path, msg, Failed)
        }
    }

    fun getChatProfiles(
        path: String,
        Failed: (msg:String) -> Unit
    ){
        getChatsProfileJob?.cancel()
        savedStateHandle["chatList"] = emptyList<Msg>()
        savedStateHandle["searchText"] = ""

        getChatsProfileJob= viewModelScope.launch(Dispatchers.IO){
            mainUseCases.getChatProfiles(
                path, {
                    savedStateHandle["chatList"] = it
                }, Failed)
        }
    }
    fun setSearchActive(visibility: Boolean){
        savedStateHandle["isSearchActive"] = visibility
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
            searchJob?.apply {
                cancel()
                searchList = mutableListOf()
            }
            searchJob=
                viewModelScope.launch(Dispatchers.IO){
                    delay(500)
                    getIdRange(id).forEachIndexed{ i,ID->
                        if(i!=0)
                            delay(i*250L+700L)
                        mainUseCases.getOrCreateUserProfileInfo(
                            ID, {user ->
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
    //val stateProfile = ViewProfileState(isLoading.collectAsState(),hasPrivateInfo,userBasicInfo)
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

    fun myBasicInfo(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.getUserBasicInfo(id,
                {
                    savedStateHandle["myBasicInfo"] = it
                },{ })
        }
    }

    fun getUserBasicInfo(id: String, Success: (profile:UserBasicInfo) -> Unit){
    savedStateHandle["hasPrivateInfo"] = false
    savedStateHandle["isLoading"] = true

    viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.getUserBasicInfo(id,
                {
                    savedStateHandle["userBasicInfo"] = it
                    Success.invoke(it)
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


    fun onSearchTextChange (text: String) {
        savedStateHandle["searchText"] = text
    }
    fun onToggleSearch() {
        savedStateHandle["isSearchActive"] = !isSearchActive.value
        if (!isSearchActive.value) {
            savedStateHandle["searchText"] = ""
        }
    }
    val userFullProfileInfo = savedStateHandle.getStateFlow("userFullProfileInfo", UserProfile())
//    val userFullResultInfo = savedStateHandle.getStateFlow("userFullResultInfo", emptyList<FullResultInfo>())
//    val userPaymentInfo = savedStateHandle.getStateFlow("userPaymentInfo", PaymentInfo())
//    val userLiveResultInfo = savedStateHandle.getStateFlow("userLiveResultInfo", emptyList<LiveResultInfo>())
//    val userRegCourseInfo = savedStateHandle.getStateFlow("userRegCourseInfo", emptyList<CourseInfo>())
    val resultLoadingTxt = savedStateHandle.getStateFlow("resultLoadingTxt", "Loading")
    val isResultLoading = savedStateHandle.getStateFlow("isResultLoading", false)


    fun getUserFullProfileInfo(
        id: String,
        Success: (profile:UserProfile) -> Unit = {},
        Failed: (msg:String) -> Unit,

    ) {
        mainUseCases.getUserFullProfile(id,{
            savedStateHandle["userFullProfileInfo"]=it
            Success.invoke(it)
        },Failed)
    }

    fun updateUserPaymentInfo(
        id: String,
        accessToken: String,
        paymentInfo: PaymentInfo,
        Failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updatePaymentInfo(id,accessToken,paymentInfo,{
                val updatedProfile = userFullProfileInfo.value.copy(paymentInfo= it)
                savedStateHandle["userFullProfileInfo"]=updatedProfile
                //getUserFullProfileInfo(id,{},Failed)
            },Failed)
        }
    }
    fun updateUserLiveResultInfo(
        id: String,
        accessToken: String,
        liveResultInfoList: List<LiveResultInfo>,
        Failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updateLiveResultInfo(id,accessToken,liveResultInfoList,{
                val updatedProfile = userFullProfileInfo.value.copy(liveResultInfo= ArrayList(it))
                savedStateHandle["userFullProfileInfo"]=updatedProfile
            },Failed)
        }

    }
    fun updateUserRegCourseInfo(
        id: String,
        accessToken: String,
        regCourseList: List<CourseInfo>,
        Failed: (msg:String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updateRegCourseInfo(id,accessToken,regCourseList,{
                val updatedProfile = userFullProfileInfo.value.copy(regCourseInfo= ArrayList(it))
                savedStateHandle["userFullProfileInfo"]=updatedProfile
            },Failed)
        }

    }

    fun updateUserFullResultInfo(
        publicInfo: PublicInfo,
        fullResultInfoList: List<FullResultInfo>,
    ){
        savedStateHandle["isResultLoading"]=true
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.updateFullResultInfo(publicInfo,fullResultInfoList,{it,cgpa->

                val updatedProfile = userFullProfileInfo.value.copy(
                    fullResultInfo= ArrayList(it), publicInfo = userFullProfileInfo.value.publicInfo.copy(cgpa = cgpa)
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
    fun getRandomHadith(Failed: (msg:String) -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            mainUseCases.getRandomHadith({
                savedStateHandle["hadith"] = it
                setDialogVisibility(true)
            },Failed)
        }
    }
    fun setShowHadith(visibility: Boolean){
        savedStateHandle["showHadith"] = visibility
    }
    fun setDialogVisibility(visibility: Boolean){
        savedStateHandle["dialogVisibility"] = visibility
    }


}