package com.mlab.knockme.main_feature.data.repo

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.util.Calendar
import kotlin.math.roundToInt

class MainRepoImpl(
    private val firebase: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val api: PortalApi
) : MainRepo {

    private lateinit var msgList: MutableList<Msg>
    private val mapper: Gson by lazy { GsonBuilder().serializeNulls().create() }
    private var searchJob: Job? =null

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getMessages(path: String): StateFlow<List<Msg>> {
        msgList = mutableListOf()
        val myRef: DatabaseReference = firebase.getReference(path)
        val mutableStateFlow: Deferred<MutableStateFlow<List<Msg>>> =
            CompletableDeferred(MutableStateFlow(msgList))
        coroutineScope {
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

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        mutableStateFlow.getCompleted().update { msgList }
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })

            } catch (e: Exception) {
                mutableStateFlow.getCompletionExceptionOrNull()
            }
        }
        return mutableStateFlow.await()
    }

    override suspend fun sendMessages(path: String, msg: Msg): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMessage(path: String, id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getChatProfiles(
        path: String,
        Success: (profileList: List<Msg>) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        Log.d("TAG", "getChatProfiles :here")
        val profileList = mutableListOf<Msg>()
        val myRef: DatabaseReference = firebase.getReference(path)
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
                val updatedData = snapshot.getValue<Msg>()!!
                profileList.forEachIndexed { i, msg ->
                    if (msg.id == updatedData.id) {
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
        Failed: (msg: String) -> Unit
    ) {
        val profileList = mutableListOf<Msg>()
        val myRef: DatabaseReference = firebase.getReference(path)
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

    override fun getUserProfileInfo(
        id: String,
        Success: (userProfile: UserProfile) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("getUserProfileInfo", "DocumentSnapshot data: ${document.data}")

                    val profile = mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java)
                    Success.invoke(profile)
                } else {
                    Log.d("getUserProfileInfo", "No Such User")
                    Failed.invoke("No user found")

                }
            }
            .addOnFailureListener { exception ->
                Log.d("getUserProfileInfo", "get failed with ", exception)
                Failed.invoke(exception.toString())
            }    }

    override fun getUserBasicInfo(
        id: String,
        Success: (userBasicInfo: UserBasicInfo) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("getUserProfileInfo", "DocumentSnapshot data: ${document.data}")

                    val userBasicInfo = mapper
                        .fromJson(mapper.toJson(document.data), UserProfile::class.java)
                        .toUserBasicInfo()
                    Success.invoke(userBasicInfo)
                } else {
                    Log.d("getUserProfileInfo", "No Such User")
                    Failed.invoke("No user found")

                }
            }
            .addOnFailureListener { exception ->
                Log.d("getUserProfileInfo", "get failed with ", exception)
                Failed.invoke(exception.toString())
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun getOrCreateUserProfileInfo(
        id: String,
        Success: (profileList: List<Msg>) -> Unit,
        Loading: (msg: String) -> Unit,
        Failed: (msg: String) -> Unit,
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists() &&
                    mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java).publicInfo.cgpa != 0.0
                ) {
                    Log.d("getOrCreateUserProfileInfo", "DocumentSnapshot data: ${document.data}")
                    val profile = mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java)
                    val shortProfile = Msg(
                        id = id,
                        nm = profile.publicInfo.nm,
                        msg = profile.publicInfo.progShortName +"   CGPA: ${profile.publicInfo.cgpa}",
                        pic = profile.privateInfo.pic,
                        time = 0L)
                    Success.invoke(listOf(shortProfile))
                } else {
                    Loading.invoke("Creating User Profile..")
                    Log.d("getOrCreateUserProfileInfo", "No user found. Creating One")
                    searchJob?.cancel()
                    searchJob = GlobalScope.launch(Dispatchers.IO){
                        try {
                            val studentInfo = api.getStudentIdInfo(id).toStudentInfo()
                            Log.d("getStudentInfo", "publicInfo: $studentInfo")
                            if(!studentInfo.studentId.isNullOrEmpty())
                            {
                                Loading.invoke("Getting CGPA Info..")
                                getCgpa(id,getSemesterList(id), {
                                    when (it) {
                                        -1 -> Failed.invoke("Oops, something went wrong.")
                                        -2 -> Failed.invoke("Couldn't reach server.")
                                        else -> Loading.invoke("Semester $it result loaded.")
                                    } },
                                    {cgpa ->
                                        var msgDis=""
                                        if(cgpa!=0.0) {
                                            msgDis = studentInfo.progShortName + "   CGPA: $cgpa"

                                        } else {
                                            msgDis = studentInfo.progShortName.toString()
                                            Loading.invoke("Server Error. Couldn't load CGPA.")
                                        }
                                        val publicInfo = PublicInfo(
                                            id = id,
                                            nm = studentInfo.studentName!!,
                                            progShortName = studentInfo.progShortName!!,
                                            batchNo = studentInfo.batchNo!!,
                                            cgpa = cgpa
                                        )
                                        val shortProfile = Msg(
                                            id = id,
                                            nm = studentInfo.studentName,
                                            msg = msgDis,
                                            pic = "", time = 0L
                                        )
                                        Success.invoke(listOf(shortProfile))
                                        docRef.set(hashMapOf("publicInfo" to publicInfo)).addOnCompleteListener {
                                            if(it.isSuccessful)
                                                Loading.invoke("User Added to Firebase.")
                                            else
                                                Loading.invoke("Firebase Server Error.")
                                        }
                                    })
                            }
                            else
                            {
                                Failed.invoke("No Student with ID- $id")
                            }

                        }catch (e: HttpException) {
                            // send(Resource.Error("Oops, something went wrong."))
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}}")
                        } catch (e: EOFException) {
                            // send(Resource.Error("Student Portal Server Error."))
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        } catch (e: IOException) {
                            // send(Resource.Error("Couldn't reach server."))
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        }
                    }


                }
            }
            .addOnFailureListener { exception ->
                Log.d("getOrCreateUserProfileInfo", "get failed with ", exception)
                Failed.invoke(exception.toString())
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun getCgpa(
        id: String,
        semesterList: List<String>,
        loading: (index: Int) -> Unit,
        success: (cgpa: Double) -> Unit
    ) {
        var weightedCgpa = 0.0
        var totalCreditWeight = 0.0
        var resultFound = 0
        semesterList.forEachIndexed { i, semesterId ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val resultInfo = api.getResultInfo(semesterId, id)
                    Log.d("TAG", "getCgpa $semesterId resultInfo: $resultInfo")
                    if (resultInfo.isNotEmpty()) {
                        loading.invoke(semesterList.size-i)
                        resultInfo.forEach { courseInfo ->
                            weightedCgpa += courseInfo.pointEquivalent * courseInfo.totalCredit
                            totalCreditWeight += courseInfo.totalCredit
                        }
                    }
                    resultFound++
                    if (resultFound == semesterList.size) {
                        var cgpa = weightedCgpa / totalCreditWeight
                        if(!cgpa.isNaN())
                            cgpa= (cgpa* 100.0).roundToInt() / 100.0
                        success.invoke(cgpa)
                    }
                } catch (e: HttpException) {
                    loading.invoke(-1)
                    Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                    this.cancel()

                }  catch (e: EOFException) {
                    success.invoke(0.0)
                    Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                    this.cancel()
                }catch (e: IOException) {
                    loading.invoke(-2)
                    Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                    this.cancel()
                }
                //this.cancel()
            }
        }
    }

    private fun getSemesterList(id: String): List<String> {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val yearEnd = year % 100
        val initial = id.slice(0..2).toInt()  //.split('-')[0].toInt()
        val yr: Int = initial / 10
        var semester: Int = initial % 10
        val list = mutableListOf<String>()

        for (y in yr..yearEnd) {
            for (s in semester..3) {
                list.add(y.toString() + s.toString())
            }
            semester = 1
        }
        return list
    }
}