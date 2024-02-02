package com.mlab.knockme.main_feature.data.repo

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.data.data_source.dto.DailyHadithDto
import com.mlab.knockme.auth_feature.domain.model.*
import com.mlab.knockme.core.util.getCgpa
import com.mlab.knockme.core.util.getSemesterList
import com.mlab.knockme.core.util.notEqualsIgnoreOrder
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import javax.inject.Inject

class MainRepoImpl @Inject constructor(
    private val firebase: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val api: PortalApi
) : MainRepo {

    private val mapper: Gson by lazy { GsonBuilder().serializeNulls().create() }
//    private var searchJob: Job? =null
//    private val searchData= mutableListOf<Msg>()
    override fun getMessages(
        path: String,
        success: (msgList: List<Msg>) -> Unit,
        failed: (msg: String) -> Unit
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
                val msg = dataSnapshot.getValue(Msg::class.java)
                msgList.add(0,msg!!)
                success.invoke(msgList)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun sendMessages(path: String, msg: Msg,failed: (msg:String) -> Unit) {
        val myRef: DatabaseReference = firebase.getReference(path)
        val key =myRef.push().key
        myRef.child(key!!).setValue(msg).addOnFailureListener {
            it.localizedMessage?.let { ex -> failed.invoke(ex) }
        }
    }

    override fun refreshProfileInChats(path: String, msg: Msg,failed: (msg:String) -> Unit) {
        val myRef: DatabaseReference = firebase.getReference(path)
        myRef.setValue(msg).addOnFailureListener {
            it.localizedMessage?.let { ex -> failed.invoke(ex) }
        }
    }

    override fun deleteMessage(path: String, id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getChatProfiles(
        path: String,
        success: (profileList: List<Msg>) -> Unit,
        failed: (msg: String) -> Unit
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
                val msg = dataSnapshot.getValue(Msg::class.java)!!
                //val msg = dataSnapshot.getValue<Map<String,String>>()!!

                profileList.add(msg)
                profileList.sortByDescending { it.time }
                success.invoke(profileList)
                Log.d("TAG", "onChildAdded: $profileList")

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedData = snapshot.getValue(Msg::class.java)!!
                profileList.find { it.id == updatedData.id }?.apply {
                    this.id=updatedData.id
                    this.nm=updatedData.nm
                    this.msg=updatedData.msg
                    this.time=updatedData.time
                    this.pic=updatedData.pic
                }
                profileList.sortByDescending { it.time }
                success.invoke(profileList)
//                val currentTimeMillis = System.currentTimeMillis()
//                val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(currentTimeMillis)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                failed.invoke(error.message)
                Log.d("TAG", "onCancelled: ${error.message}")
            }
        })
    }

    override fun getUserBasicInfo(
        id: String,
        success: (userBasicInfo: UserBasicInfo) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        Log.d("getUserBasicInfo", "Data: call")

        val docRef = firestore.collection("userProfile").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    val userBasicInfo = mapper
                        .fromJson(mapper.toJson(document.data), UserProfile::class.java)
                        .toUserBasicInfo()
                    Log.d("getUserBasicInfo", "DocumentSnapshot data: ${userBasicInfo.publicInfo}")

                    success.invoke(userBasicInfo)
                } else {
                    Log.d("getUserBasicInfo", "No Such User")
                    getOrCreateUserProfileInfo(id,{
                        if(!it.id.isNullOrEmpty())
                            getUserBasicInfo(it.id!!,success, failed)
                    },{},failed)
                    failed.invoke("No user found")

                }
            }
            .addOnFailureListener { exception ->
                Log.d("getUserBasicInfo", "get failed with ", exception)
                failed.invoke(exception.toString())
            }
    }

    override fun getUserFullProfile(
        id: String,
        success: (UserProfile) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("getOrCreateUserProfileInfo", "DocumentSnapshot data: ${document.data}")
                    val profile = mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java)
                    success.invoke(profile)
                } else { failed.invoke("User Doesn't Exist") }
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> failed.invoke(it1) }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun getOrCreateUserProfileInfo(
        id: String,
        success: (Msg) -> Unit,
        loading: (msg: String) -> Unit,
        failed: (msg: String) -> Unit,
    ) {
        loading.invoke("Verifying ID- $id")
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
                        loading.invoke("Server Error. Couldn't Calculate CGPA for ID- $id. Retrying..")
                    } else {
                        msgDis = "ID: ${profile.publicInfo.id}      CGPA: ${profile.publicInfo.cgpa}"
                        loadFromPortal = false
                        loading.invoke("Loaded Result From Backup Server For ID- $id.")
                    }
                    val shortProfile = Msg(
                        id = id,
                        nm = profile.publicInfo.nm,
                        msg = msgDis,
                        pic = profile.privateInfo.pic,
                        time = profile.lastUpdatedResultInfo)
                    //searchData.add(0,shortProfile)
                    success.invoke(shortProfile)

                }
                if(loadFromPortal) {
                    Log.d("getOrCreateUserProfileInfo", "No user found. Creating One")
//                    searchJob?.cancel()
//                    searchJob =
                    GlobalScope.launch(Dispatchers.IO){
                        try {
                            val result = api.getStudentIdInfo(id)
                            val studentInfo = result.body()?.toStudentInfo()
                            Log.d("getStudentInfo", "publicInfo: $studentInfo")
                            if(result.isSuccessful && studentInfo != null && studentInfo.firstSemId != null)
                            {
                                loading.invoke("ID- $id Is Valid. Getting CGPA Info..")
                                getCgpa(
                                    id = id,
                                    api,
                                    semesterList = getSemesterList(studentInfo.firstSemId.toInt()),
                                    loading = {
                                    when (it) {
                                        -1 -> failed.invoke("Oops, Something Went Wrong.")
                                        -2 -> failed.invoke("Couldn't Reach Server.")
                                        else -> loading.invoke("Getting SGPA For $id. loading Done For Semester $it .")
                                    } },
                                    success = { cgpa,totalCompletedCredit, fullResultInfo ->
                                        val msgDis: String
                                        if(cgpa!=0.0) {
                                            msgDis = "ID: ${studentInfo.studentId}      CGPA: $cgpa"
                                        } else {
                                            msgDis = "ID: ${studentInfo.studentId}"
                                            loading.invoke("Server Error. Couldn't Get All Semester SGPA For - $id.")
                                        }
                                        val publicInfo = PublicInfo(
                                            id = id,
                                            nm = studentInfo.studentName!!,
                                            progShortName = studentInfo.progShortName!!,
                                            batchNo = studentInfo.batchNo!!,
                                            cgpa = cgpa,
                                            totalCompletedCredit =totalCompletedCredit,
                                            firstSemId = studentInfo.firstSemId.toInt()
                                        )
                                        val shortProfile = Msg(
                                            id = id,
                                            nm = studentInfo.studentName,
                                            msg = msgDis,
                                            pic = "",
                                            time = System.currentTimeMillis()
                                        )
                                        //searchData.add(0,shortProfile)
                                        success.invoke(shortProfile)
                                        if(!document.exists()) {
                                            docRef.set(hashMapOf("publicInfo" to publicInfo)).addOnCompleteListener {
                                                if(it.isSuccessful) {
                                                    firestore
                                                        .collection("public")
                                                        .document("info")
                                                        .update("profileCount", FieldValue.increment(1))
                                                    loading.invoke("Info for $id Added To Backup Server.")

                                                } else
                                                    loading.invoke("Firebase Server Error. Couldn't Save Info For $id To Backup Server.")
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
                                failed.invoke("No Student with ID- $id")
                            }

                        }catch (e: HttpException) {
                            failed.invoke("Oops, Something Went Wrong.")
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}}")
                        } catch (e: EOFException) {
                            failed.invoke("Student Portal Server Error.")
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        } catch (e: IOException) {
                            failed.invoke("Couldn't Reach Server.")
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        }
                    }


                }
            }
            .addOnFailureListener { exception ->
                Log.d("getOrCreateUserProfileInfo", "get failed with ", exception)
                failed.invoke(exception.toString())
            }
    }

    override suspend fun updatePaymentInfo(
        id: String,
        accessToken: String,
        paymentInfo: PaymentInfo,
        success: (PaymentInfo) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        try {
            Log.d("token--", "updatePaymentInfo: $accessToken")
            val paymentInfoNew = api.getPaymentInfo(accessToken)?.toPaymentInfo()
            Log.d("getStudentInfo", "paymentInfo: $paymentInfoNew")
            if(paymentInfo != paymentInfoNew && paymentInfoNew!= null) {
                success.invoke(paymentInfoNew)
                docRef.update("paymentInfo" , paymentInfoNew)
                docRef.update("lastUpdatedPaymentInfo" , System.currentTimeMillis())
            }
            else failed.invoke("No new data found.")
        }
        catch (e: HttpException) {
            failed.invoke("Oops, something went wrong.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: EOFException) {
            failed.invoke("Student Portal Server Error.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: IOException) {
            failed.invoke("Couldn't reach server.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        }
    }

    override suspend fun updateRegCourseInfo(
        id: String,
        accessToken: String,
        regCourseInfoList: List<CourseInfo>,
        success: (List<CourseInfo>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        try {
            val semInfo =api.getAllSemesterInfo(accessToken)
            var regCourseInfoNew = emptyList<CourseInfo>()
            run lit@{
                semInfo?.forEach {lsInfo ->
                    val result = api.getRegisteredCourse(lsInfo.semesterId, accessToken)?.map { it.toCourseInfo() }
                    result?.let {
                        regCourseInfoNew = result
                    }
                    if(regCourseInfoNew.isNotEmpty())
                        return@lit
                }
            }
            Log.d("getStudentInfo", "registeredCourse: $regCourseInfoNew")
            if (regCourseInfoList notEqualsIgnoreOrder regCourseInfoNew && regCourseInfoNew.isNotEmpty()){
                success.invoke(regCourseInfoNew)
                docRef.update("regCourseInfo" , regCourseInfoNew)
                docRef.update("lastUpdatedRegCourseInfo" , System.currentTimeMillis())
            }
            else failed.invoke("No new data found.")


        }
        catch (e: HttpException) {
            failed.invoke("Oops, something went wrong.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: EOFException) {
            failed.invoke("Student Portal Server Error.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: IOException) {
            failed.invoke("Couldn't reach server.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        }
    }

    override suspend fun updateLiveResultInfo(
        id: String,
        accessToken: String,
        liveResultInfoList: List<LiveResultInfo>,
        success: (List<LiveResultInfo>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        val liveResultInfoListNew = mutableListOf<LiveResultInfo>()
        try {
            val semInfo =api.getAllSemesterInfo(accessToken)
            var registeredCourse = emptyList<CourseInfo>()
            run lit@{
                semInfo?.forEach {lsInfo ->
                    val result = api.getRegisteredCourse(lsInfo.semesterId, accessToken)?.map { it.toCourseInfo() }
                    result?.let {
                        registeredCourse = result
                    }
                    if(registeredCourse.isNotEmpty())
                        return@lit
                }
            }
            Log.d("getStudentInfo", "registeredCourse: $registeredCourse")
            registeredCourse.forEach {
                val result = api.getLiveResult(it.courseSectionId!!, accessToken)
                val data = result.body()
                if(result.isSuccessful && data != null)
                    liveResultInfoListNew.add(
                        data.toLiveResultInfo(it.customCourseId!!, it.courseTitle!!, it.toShortSemName())
                    )
            }
            if(liveResultInfoList notEqualsIgnoreOrder liveResultInfoListNew && liveResultInfoListNew.isNotEmpty()){
                success.invoke(liveResultInfoListNew)
                docRef.update("liveResultInfo" , liveResultInfoListNew)
                docRef.update("lastUpdatedLiveResultInfo" , System.currentTimeMillis())
            }
            else failed.invoke("No new data found.")

        }
        catch (e: HttpException) {
            failed.invoke("Oops, something went wrong.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: EOFException) {
            failed.invoke("Student Portal Server Error.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: IOException) {
            failed.invoke("Couldn't reach server.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        }
    }

    override suspend fun updateFullResultInfo(
        publicInfo: PublicInfo,
        fullResultInfoList: List<FullResultInfo>,
        success: (List<FullResultInfo>,Double, Double) -> Unit,
        loading: (msg: String) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(publicInfo.id!!)
        getCgpa(publicInfo.id!!,api,getSemesterList(publicInfo.firstSemId),{
            when (it) {
                -1 -> failed.invoke("Oops, something went wrong.")
                -2 -> failed.invoke("Couldn't reach server.")
                else -> loading.invoke("Semester $it result loaded.")
            }
        },{cgpa,totalCompletedCredit, fullResultInfoListNew ->
            Log.d("TAG", "updateFullResultInfo: $cgpa")

            if(fullResultInfoListNew.size >= fullResultInfoList.size){
                if((fullResultInfoList notEqualsIgnoreOrder fullResultInfoListNew && fullResultInfoListNew.isNotEmpty()) || (publicInfo.cgpa!=cgpa && cgpa!=0.0)){
                    success.invoke(fullResultInfoListNew, cgpa, totalCompletedCredit)
                    docRef.update("fullResultInfo" , fullResultInfoListNew)
                    docRef.update("publicInfo" , publicInfo.copy(cgpa = cgpa, totalCompletedCredit = totalCompletedCredit))
                    docRef.update("lastUpdatedResultInfo" , System.currentTimeMillis())
                }
                else failed.invoke("No new data found.")
            }
            else failed.invoke("No new data found.")

        })
    }

    override suspend fun updateClearanceInfo(
        id: String,
        accessToken: String,
        clearanceInfoList: List<ClearanceInfo>,
        success: (clearanceInfoList: List<ClearanceInfo>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(id)
        try {
            val clearanceInfoNew =api.getClearanceInfo(accessToken)?.map { it.toClearanceInfo() }
            Log.d("getStudentInfo", "clearanceInfoList: $clearanceInfoNew")
            if (clearanceInfoNew!= null && clearanceInfoList notEqualsIgnoreOrder clearanceInfoNew && clearanceInfoNew.isNotEmpty()){
                success.invoke(clearanceInfoNew)
                docRef.update("clearanceInfo" , clearanceInfoNew)
                docRef.update("lastUpdatedClearanceInfo" , System.currentTimeMillis())
            }
            else failed.invoke("No new data found.")


        }
        catch (e: HttpException) {
            failed.invoke("Oops, something went wrong.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: EOFException) {
            failed.invoke("Student Portal Server Error.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        } catch (e: IOException) {
            failed.invoke("Couldn't reach server.")
            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
        }
    }

    override suspend fun getRandomHadith(
        success: (DailyHadithDto) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        try {
            val hadith = api.getDailyHadith()?.random()
            Log.d("getRandomHadith", "hadith: $hadith")
            if(hadith != null)
                success.invoke(hadith)
            else
                failed.invoke("Oops, something went wrong.")
        }
        catch (e: HttpException) {
            failed.invoke("Oops, something went wrong.")
            Log.d("TAG", "getRandomHadith: ${e.message} ${e.localizedMessage}")
        } catch (e: EOFException) {
            failed.invoke("Student Portal Server Error.")
            Log.d("TAG", "getRandomHadith: ${e.message} ${e.localizedMessage}")
        } catch (e: IOException) {
            failed.invoke("Couldn't reach server.")
            Log.d("TAG", "getRandomHadith: ${e.message} ${e.localizedMessage}")
        }
    }
}