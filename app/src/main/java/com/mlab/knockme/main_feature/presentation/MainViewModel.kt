package com.mlab.knockme.main_feature.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.use_case.MainUseCases
import com.mlab.knockme.main_feature.presentation.chats.components.ChatListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainUseCases: MainUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _msg= MutableStateFlow<List<Msg>>(emptyList())
    val msg = _msg.asStateFlow()

    private val chatList = savedStateHandle.getStateFlow("chatList", emptyList<Msg>())
    val chatListState = chatList

    private val searchText = savedStateHandle.getStateFlow("searchText", "")
    private val isSearchActive = savedStateHandle.getStateFlow("isSearchActive", false)

    val state = combine(chatList,searchText,isSearchActive){chatList,searchText,isSearchActive ->
        ChatListState(chatList, searchText, isSearchActive) //1 searchNotes.execute(notes, searchText) ,
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(0) , ChatListState())
    private var getMsgJob: Job? =null
    private var getChatsProfileJob: Job? =null
    private var getChatsJob: Job? =null
    private var searchJob: Job? =null




    suspend fun getMeg(path: String) {
        getMsgJob?.cancel()
        getMsgJob= mainUseCases.getMsg(path)
            .onEach {
                _msg.value =it
            }
            .launchIn(viewModelScope)
    }

    fun getChatProfiles(
        path: String,
        Failed: (msg:String) -> Unit
    ){
        getChatsProfileJob?.cancel()
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
        savedStateHandle["isSearchActive"] = true
        savedStateHandle["chatList"] = searchList
        if (id.length>9){
            searchJob?.cancel()
            searchJob=
                viewModelScope.launch{
                    delay(100)
                    getIdRange(id).forEachIndexed{ i,ID->
                        delay(200)
                        mainUseCases.getOrCreateUserProfileInfo(
                            ID, {msg ->
                                Log.d("TAG69", "searchUser: $msg")
                                if(msg.id==id)
                                    searchList.add(0,msg)
                                else
                                    searchList.add(msg)
                                //searchList.sortBy { it.id }  //Array index out of range: 11
                                val x =mutableListOf<Msg>()
                                x.addAll(searchList)
                                savedStateHandle["chatList"] = x
                            }, Loading ,Failed
                        )
                    }
                }

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

    fun onSearchTextChange (text: String) {
        savedStateHandle["searchText"] = text
    }
    fun onToggleSearch() {
        savedStateHandle["isSearchActive"] = !isSearchActive.value
        if (!isSearchActive.value) {
            savedStateHandle["searchText"] = ""
        }
    }

    fun getChats(
        path: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg:String) -> Unit
    ){
        getChatsJob?.cancel()
        getChatsJob= viewModelScope.launch{
            mainUseCases.getChats(path, Success, Failed)
        }
    }
}