package com.mlab.knockme.main_feature.data.repo

import java.text.SimpleDateFormat
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.Locale

class MainRepoImpl(
    private val database:FirebaseDatabase,
    private val api :PortalApi
): MainRepo {

    private lateinit var msgList: MutableList<Msg>
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getMessages(path: String): StateFlow<List<Msg>> {
        msgList= mutableListOf()
        val myRef: DatabaseReference = database.getReference(path)
        val mutableStateFlow :Deferred<MutableStateFlow<List<Msg>>> = CompletableDeferred(MutableStateFlow(msgList))
        coroutineScope{
            try {
//                    mutableStateFlow.emit(SignInResponse.Loading)
                myRef.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(
                        dataSnapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        Log.d("TAG", "onChildAdded:" + dataSnapshot.key!!)
                        val msg = dataSnapshot.getValue<Msg>()
                        msgList.add(msg!!)
                    }
                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        mutableStateFlow.getCompleted().update { msgList }
                    }
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })

            }
            catch (e:Exception)
            {
                mutableStateFlow.getCompletionExceptionOrNull()
            }
        }
        return mutableStateFlow.await()
    }

    override suspend fun sendMessages(path: String, msg: Msg): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMessage(path: String, id: String): Boolean{
        TODO("Not yet implemented")
    }

    override fun getChatProfiles(
        path: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg:String) -> Unit
    ) {
        Log.d("TAG", "getChatProfiles :here")
        val profileList = mutableListOf<Msg>()
        val myRef: DatabaseReference = database.getReference(path)
        myRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                dataSnapshot: DataSnapshot,
                previousChildName: String?
            ) {
                Log.d("TAG", "onChildAdded:" + dataSnapshot.key!!)
                val msg = dataSnapshot.getValue<Msg>()!!
                //val msg = dataSnapshot.getValue<Map<String,String>>()!!

                profileList.add(msg)
                profileList.sortByDescending { it.time }
                Success.invoke(profileList)
                Log.d("TAG", "onChildAdded: $profileList")

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedData=snapshot.getValue<Msg>()!!
                profileList.forEachIndexed{i,msg ->
                    if(msg.id==updatedData.id)
                    {
                        profileList.removeAt(i)
                        profileList.add(updatedData)
                        return@forEachIndexed
                    }
                }
                profileList.sortByDescending { it.time }
                Success.invoke(profileList)
//                val currentTimeMillis = System.currentTimeMillis()
//                val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(currentTimeMillis)
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Failed.invoke(error.message)
                Log.d("TAG", "onCancelled: ${error.message}")
            }
        })
    }

    override fun getChats(
        path: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg:String) -> Unit
    ) {
        val profileList = mutableListOf<Msg>()
        val myRef: DatabaseReference = database.getReference(path)
        myRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                dataSnapshot: DataSnapshot,
                previousChildName: String?
            ) {
                Log.d("TAG", "onChildAdded:" + dataSnapshot.key!!)
                val msg = dataSnapshot.getValue<Msg>()!!
                profileList.add(msg)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Success.invoke(profileList)
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Failed.invoke(error.message)
            }
        })
    }

    override fun searchById(
        id: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}