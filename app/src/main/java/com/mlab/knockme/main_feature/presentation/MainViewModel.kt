package com.mlab.knockme.main_feature.presentation

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlab.knockme.auth_feature.domain.model.CourseInfo
import com.mlab.knockme.auth_feature.domain.model.LiveResultInfo
import com.mlab.knockme.auth_feature.domain.model.PaymentInfo
import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.main_feature.domain.model.*
import com.mlab.knockme.main_feature.domain.use_case.MainUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val state = combine(chatList,searchText,isSearchActive,loadingText)
    {chatList,searchText,isSearchActive,loadingText ->
        ChatListState(chatList, searchText, isSearchActive,loadingText) //1 searchNotes.execute(notes, searchText) ,
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
        getMsgJob = viewModelScope.launch {
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
        viewModelScope.launch {
            //savedStateHandle["msgText"] = msg.msg
            mainUseCases.sendMsg(path, msg, Failed)
        }
    }
    fun refreshProfileInChats(path: String, msg: Msg, Failed: (msg: String) -> Unit) {
        viewModelScope.launch {
            //savedStateHandle["msgText"] = msg.msg
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

        getChatsProfileJob= viewModelScope.launch{
            mainUseCases.getChatProfiles(
                path, {
                    savedStateHandle["chatList"] = it
                }, Failed)
        }
    }

    fun searchUser(
        id: String,
        Loading: (msg: String) -> Unit,
        Failed: (msg:String) -> Unit
    ){
        val searchList= mutableListOf<Msg>()
        savedStateHandle["chatList"] = searchList
        savedStateHandle["searchText"] = id

        if(id.length<2)
            savedStateHandle["isSearchActive"] = false
        if (id.length>9){
            var userCount=10
            var done=0
            savedStateHandle["isSearchActive"] = true
            searchJob?.cancel()
            searchJob=
                viewModelScope.launch{
                    delay(500)
                    getIdRange(id).forEachIndexed{ i,ID->
                        if(i!=0)
                            delay(i*120L)
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
        viewModelScope.launch {
            delay(1000)
            savedStateHandle["isSearchActive"] = false

        }
    }
    private fun getIdRange(id: String): List<String>{
        val idRange = mutableListOf(id)
        val last = id.last().toString()
        val idWithOutLast =id.dropLast(1)
        for(i in 0..9){
            if(i!=last.toInt())
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

    //val userBasicInfo = _userBasicInfo
    //val stateProfile = ViewProfileState(isLoading.collectAsState(),hasPrivateInfo,userBasicInfo)
    //var stateProfile :StateFlow<ViewProfileState>? = null

//    fun clearUserBasicInfo(){
//        stateProfile = combine(isLoading,hasPrivateInfo,userBasicInfo)
//        {x,y,z -> ViewProfileState(x, y, z) }
//            .stateIn(viewModelScope, SharingStarted.Eagerly, ViewProfileState())
//    }
    fun getTarBasicInfo(id: String){
    viewModelScope.launch {
        mainUseCases.getUserBasicInfo(id,
            {
                savedStateHandle["tarBasicInfo"] = it
            },{ })
        }
    }

    fun getUserBasicInfo(id: String){
    savedStateHandle["hasPrivateInfo"] = false
    savedStateHandle["isLoading"] = true

    viewModelScope.launch {
            mainUseCases.getUserBasicInfo(id,
                {
                    savedStateHandle["userBasicInfo"] = it
                    viewModelScope.launch {
                        delay(1600)
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
    val userPaymentInfo = savedStateHandle.getStateFlow("userPaymentInfo", PaymentInfo())
    val userLiveResultInfo = savedStateHandle.getStateFlow("userLiveResultInfo", emptyList<LiveResultInfo>())
    val userRegCourseInfo = savedStateHandle.getStateFlow("userRegCourseInfo", emptyList<CourseInfo>())

    fun getUserFullProfileInfo(id: String, Failed: (msg:String) -> Unit) {
        mainUseCases.getUserFullProfile(id,{
            savedStateHandle["userFullProfileInfo"]=it
        },Failed)
    }

    suspend fun getOrUpdateUserPaymentInfo(
        id: String,
        accessToken: String,
        paymentInfo: PaymentInfo,
        Failed: (msg:String) -> Unit
    ){
        mainUseCases.updatePaymentInfo(id,accessToken,paymentInfo,{
            savedStateHandle["userPaymentInfo"]=it
            getUserFullProfileInfo(id, Failed)
        },Failed)
    }


}