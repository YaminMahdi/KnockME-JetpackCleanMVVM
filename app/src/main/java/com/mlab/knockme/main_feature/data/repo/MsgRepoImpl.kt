package com.mlab.knockme.main_feature.data.repo

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MsgRepo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MsgRepoImpl(private val database:FirebaseDatabase): MsgRepo {

    private lateinit var msgList: MutableList<Msg>
    private lateinit var myRef: DatabaseReference
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getMessages(path: String): StateFlow<List<Msg>> {
        msgList= mutableListOf()
        myRef = database.getReference(path)
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
}