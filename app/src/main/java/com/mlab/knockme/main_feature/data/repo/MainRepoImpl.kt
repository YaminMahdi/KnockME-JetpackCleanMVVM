package com.mlab.knockme.main_feature.data.repo

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.domain.model.*
import com.mlab.knockme.core.util.notEqualsIgnoreOrder
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.roundToInt

class MainRepoImpl @Inject constructor(
    private val firebase: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val api: PortalApi
) : MainRepo {

    private val mapper: Gson by lazy { GsonBuilder().serializeNulls().create() }
//    private var searchJob: Job? =null
//    private val searchData= mutableListOf<Msg>()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getMessages(path: String): StateFlow<List<Msg>> {
        val msgList = mutableListOf<Msg>()
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

    override fun getMessages(
        path: String,
        Success: (msgList: List<Msg>) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val myRef: DatabaseReference = firebase.getReference(path)
        val msgList = mutableListOf<Msg>()
        myRef
            .limitToLast(50)
            .addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                dataSnapshot: DataSnapshot,
                previousChildName: String?
            ) {
                Log.d("TAG", "onChildAdded:" + dataSnapshot.key!!)
                val msg = dataSnapshot.getValue<Msg>()
                msgList.add(msg!!)
                Success.invoke(msgList)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun sendMessages(path: String, msg: Msg,Failed: (msg:String) -> Unit) {
        val myRef: DatabaseReference = firebase.getReference(path)
        val key =myRef.push().key
        myRef.child(key!!).setValue(msg).addOnFailureListener {
            it.localizedMessage?.let { ex -> Failed.invoke(ex) }
        }
    }

    override fun deleteMessage(path: String, id: String): Boolean {
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

    override fun getUserBasicInfo(
        id: String,
        Success: (userBasicInfo: UserBasicInfo) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
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

    override fun getUserFullProfile(
        id: String,
        Success: (UserProfile) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("getOrCreateUserProfileInfo", "DocumentSnapshot data: ${document.data}")
                    val profile = mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java)
                    Success.invoke(profile)
                } else { Failed.invoke("User Doesn't Exist") }
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Failed.invoke(it1) }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun getOrCreateUserProfileInfo(
        id: String,
        Success: (Msg) -> Unit,
        Loading: (msg: String) -> Unit,
        Failed: (msg: String) -> Unit,
    ) {
        Loading.invoke("Verifying ID- $id")
        val docRef = firestore.collection("userProfile").document(id)
        var loadFromPortal=true
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("getOrCreateUserProfileInfo", "DocumentSnapshot data: ${document.data}")
                    val profile = mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java)
                    val msgDis: String
                    if(profile.publicInfo.cgpa == 0.0) {
                        msgDis = "ID: ${profile.publicInfo.id}"
                        Loading.invoke("Server Error. Couldn't Calculate CGPA for ID- $id. Retrying..")
                    } else {
                        msgDis = "ID: ${profile.publicInfo.id}      CGPA: ${profile.publicInfo.cgpa}"
                        loadFromPortal = false
                        Loading.invoke("Loaded Result From Backup Server For ID- $id.")
                    }
                    val shortProfile = Msg(
                        id = id,
                        nm = profile.publicInfo.nm,
                        msg = msgDis,
                        pic = profile.privateInfo.pic,
                        time = profile.lastUpdatedResultInfo)
                    //searchData.add(0,shortProfile)
                    Success.invoke(shortProfile)

                }
                if(loadFromPortal) {
                    Log.d("getOrCreateUserProfileInfo", "No user found. Creating One")
//                    searchJob?.cancel()
//                    searchJob =
                    GlobalScope.launch(Dispatchers.IO){
                        try {
                            val studentInfo = api.getStudentIdInfo(id).toStudentInfo()
                            Log.d("getStudentInfo", "publicInfo: $studentInfo")
                            if(!studentInfo.studentId.isNullOrEmpty())
                            {
                                Loading.invoke("ID- $id Is Valid. Getting CGPA Info..")
                                getCgpa(
                                    id = id,
                                    loading = {
                                    when (it) {
                                        -1 -> Failed.invoke("Oops, Something Went Wrong.")
                                        -2 -> Failed.invoke("Couldn't Reach Server.")
                                        else -> Loading.invoke("Getting SGPA For $id. Loading Done For Semester $it .")
                                    } },
                                    success = { cgpa, fullResultInfo ->
                                        val msgDis: String
                                        if(cgpa!=0.0) {
                                            msgDis = "ID: ${studentInfo.studentId}      CGPA: $cgpa"
                                        } else {
                                            msgDis = "ID: ${studentInfo.studentId}"
                                            Loading.invoke("Server Error. Couldn't Get All Semester SGPA For - $id.")
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
                                            pic = "",
                                            time = System.currentTimeMillis()
                                        )
                                        //searchData.add(0,shortProfile)
                                        Success.invoke(shortProfile)
                                        if(!document.exists()) {
                                            docRef.set(hashMapOf("publicInfo" to publicInfo)).addOnCompleteListener {
                                                if(it.isSuccessful) {
                                                    firestore
                                                        .collection("public")
                                                        .document("info")
                                                        .update("profileCount", FieldValue.increment(1))
                                                    Loading.invoke("Info for $id Added To Backup Server.")

                                                } else
                                                    Loading.invoke("Firebase Server Error. Couldn't Save Info For $id To Backup Server.")
                                            }
                                            docRef.update("fullResultInfo" , fullResultInfo)
                                            docRef.update("lastUpdatedResultInfo" , System.currentTimeMillis())

                                        } else {
                                            docRef.update("publicInfo" , publicInfo)
                                            docRef.update("fullResultInfo" , fullResultInfo)
                                            docRef.update("lastUpdatedResultInfo" , System.currentTimeMillis())
                                        }
                                    })
                            }
                            else {
                                Failed.invoke("No Student with ID- $id")
                            }

                        }catch (e: HttpException) {
                            Failed.invoke("Oops, Something Went Wrong.")
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}}")
                        } catch (e: EOFException) {
                            Failed.invoke("Student Portal Server Error.")
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        } catch (e: IOException) {
                            Failed.invoke("Couldn't Reach Server.")
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

    override suspend fun updatePaymentInfo(
        id: String,
        accessToken: String,
        paymentInfo: PaymentInfo,
        Success: (PaymentInfo) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        try {
            val paymentInfoNew = api.getPaymentInfo(accessToken).toPaymentInfo()
            Log.d("getStudentInfo", "paymentInfo: $paymentInfoNew")
            if(paymentInfo != paymentInfoNew) {
                Success.invoke(paymentInfoNew)
                docRef.update("paymentInfo" , paymentInfoNew)
                docRef.update("lastUpdatedPaymentInfo" , System.currentTimeMillis())
            }
            else Failed.invoke("No new data found.")
        }
        catch (e: HttpException) {
            Failed.invoke("Oops, something went wrong.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: EOFException) {
            Failed.invoke("Student Portal Server Error.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: IOException) {
            Failed.invoke("Couldn't reach server.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        }
    }

    override suspend fun updateRegCourseInfo(
        id: String,
        accessToken: String,
        regCourseInfoList: List<CourseInfo>,
        Success: (List<CourseInfo>) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        try {
            val lastSemesterId = api.getAllSemesterInfo(accessToken)[0].semesterId
            Log.d("getStudentInfo", "lastSemesterId: $lastSemesterId")
            val regCourseInfoNew = api.getRegisteredCourse(lastSemesterId, accessToken).map { it.toCourseInfo() }
            Log.d("getStudentInfo", "registeredCourse: $regCourseInfoNew")
            if (regCourseInfoList notEqualsIgnoreOrder regCourseInfoNew){
                Success.invoke(regCourseInfoNew)
                docRef.update("regCourseInfo" , regCourseInfoNew)
                docRef.update("lastUpdatedRegCourseInfo" , System.currentTimeMillis())
            }
            else Failed.invoke("No new data found.")


        }
        catch (e: HttpException) {
            Failed.invoke("Oops, something went wrong.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: EOFException) {
            Failed.invoke("Student Portal Server Error.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: IOException) {
            Failed.invoke("Couldn't reach server.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        }    }

    override suspend fun updateLiveResultInfo(
        id: String,
        accessToken: String,
        liveResultInfoList: List<LiveResultInfo>,
        Success: (List<LiveResultInfo>) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        val liveResultInfoListNew = mutableListOf<LiveResultInfo>()
        try {
            val lastSemesterId = api.getAllSemesterInfo(accessToken)[0].semesterId
            Log.d("getStudentInfo", "lastSemesterId: $lastSemesterId")
            val registeredCourse = api.getRegisteredCourse(lastSemesterId, accessToken).map { it.toCourseInfo() }
            Log.d("getStudentInfo", "registeredCourse: $registeredCourse")
            registeredCourse.forEach {
                liveResultInfoListNew.add(api
                    .getLiveResult(it.courseSectionId!!, accessToken)
                    .toLiveResultInfo(it.customCourseId!!, it.courseTitle!!, it.toShortSemName())
                )
            }
            if(liveResultInfoList notEqualsIgnoreOrder liveResultInfoListNew){
                Success.invoke(liveResultInfoListNew)
                docRef.update("liveResultInfo" , liveResultInfoListNew)
                docRef.update("lastUpdatedLiveResultInfo" , System.currentTimeMillis())
            }
            else Failed.invoke("No new data found.")

        }
        catch (e: HttpException) {
            Failed.invoke("Oops, something went wrong.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: EOFException) {
            Failed.invoke("Student Portal Server Error.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: IOException) {
            Failed.invoke("Couldn't reach server.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        }    }

    override suspend fun updateFullResultInfo(
        publicInfo: PublicInfo,
        fullResultInfoList: List<FullResultInfo>,
        Success: (List<FullResultInfo>) -> Unit,
        Loading: (msg: String) -> Unit,
        Failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(publicInfo.id!!)
        getCgpa(publicInfo.id!!,getSemesterList(publicInfo.id!!),{
            when (it) {
                -1 -> Failed.invoke("Oops, something went wrong.")
                -2 -> Failed.invoke("Couldn't reach server.")
                else -> Loading.invoke("Semester $it result loaded.")
            }
        },{cgpa, fullResultInfoListNew ->
            publicInfo.cgpa=cgpa
            if(fullResultInfoList notEqualsIgnoreOrder fullResultInfoListNew){
                Success.invoke(fullResultInfoListNew)
                docRef.update("fullResultInfo" , fullResultInfoListNew)
                docRef.update("publicInfo" , publicInfo)
                docRef.update("lastUpdatedResultInfo" , System.currentTimeMillis())
            }
            else Failed.invoke("No new data found.")

        })
    }


    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun getCgpa(
        id: String,
        semesterList: List<String> = getSemesterList(id),
        loading: (index: Int) -> Unit,
        success: (cgpa: Double,fullResultInfo : List<FullResultInfo>) -> Unit
    ) {
        var weightedCgpa = 0.0
        var totalCreditWeight = 0.0
        var resultFound = 0
        val fullResultInfo = mutableListOf<FullResultInfo>()
        semesterList.forEachIndexed { i, semesterId ->
            GlobalScope.launch(Dispatchers.IO) {
                val semesterResultInfo = FullResultInfo()
                try {
                    delay(i*200.toLong())
                    val resultInfo = api.getResultInfo(semesterId, id)
                    Log.d("TAG", "getCgpa $semesterId resultInfo: $resultInfo")
                    if (resultInfo.isNotEmpty()) {

                        loading.invoke(semesterList.size-i)
                        var creditTaken = 0.0
                        val rInfo = arrayListOf<ResultInfo>()
                        resultInfo.forEach { courseInfo ->
                            weightedCgpa += courseInfo.pointEquivalent * courseInfo.totalCredit
                            creditTaken += courseInfo.totalCredit
                            rInfo.add(courseInfo.toResultInfo())
                        }
                        totalCreditWeight += creditTaken

                        //add a semester result to list
                        semesterResultInfo.resultInfo = rInfo
                        semesterResultInfo.semesterInfo = resultInfo[0].toSemesterInfo(creditTaken)
                        fullResultInfo.add(semesterResultInfo)  //adding
                    }
                    resultFound++
                    if (resultFound == semesterList.size) {
                        var cgpa = weightedCgpa / totalCreditWeight
                        if(!cgpa.isNaN())
                            cgpa= (cgpa* 100.0).roundToInt() / 100.0
                        success.invoke(cgpa,fullResultInfo)
                    }
                } catch (e: HttpException) {
                    loading.invoke(-1)
                    Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                    this.cancel()

                }  catch (e: EOFException) {
                    success.invoke(0.0, emptyList())
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
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val endYearSemesterCount =
            if (month < 3) 0
            else if (month < 7) 1
            else if (month < 11) 2
            else 3

        val yearEnd = year % 100
        val initial = id.slice(0..2).toInt()  //.split('-')[0].toInt()
        val yr: Int = initial / 10
        var semester: Int = initial % 10
        val list = mutableListOf<String>()

        for (y in yr..yearEnd) {
            for (s in semester..3) {
                if (y == yearEnd && s > endYearSemesterCount)
                    break
                list.add(y.toString() + s.toString())
            }
            semester = 1
        }
        return list
    }
}