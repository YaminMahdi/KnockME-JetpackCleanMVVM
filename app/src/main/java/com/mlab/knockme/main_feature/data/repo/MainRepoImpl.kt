package com.mlab.knockme.main_feature.data.repo

import android.util.Log
import androidx.core.content.edit
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.data.data_source.dto.DailyHadithDto
import com.mlab.knockme.auth_feature.domain.model.*
import com.mlab.knockme.core.util.*
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.model.UserBasicInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import com.mlab.knockme.pref
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import javax.inject.Inject

class MainRepoImpl @Inject constructor(
    private val firebase: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val api: PortalApi
) : MainRepo {

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
                val userBasicInfo = document.toObject<UserProfile>()?.toUserBasicInfo()
                if (userBasicInfo != null) {
                    Log.d("getUserBasicInfo", "DocumentSnapshot data: ${userBasicInfo.publicInfo}")
                    success.invoke(userBasicInfo)
                } else {
                    Log.d("getUserBasicInfo", "No Such User")
                    getOrCreateUserProfileInfo(id, null,{ msg->
                        if(!msg.id.isNullOrEmpty())
                            getUserBasicInfo(msg.id!!,success, failed)
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
                    val user = document.toObject<UserProfile>() ?: UserProfile()
                    CoroutineScope(Dispatchers.IO).launch {
                        if (user.publicInfo.programId.isEmpty()){
                            var info = tryGet { api.getStudentIdInfo(id).body() }
                            if ((info == null || info.studentId.isNullOrEmpty()) && user.privateInfo.email != null) {
                                val realStudentId = user.privateInfo.email!!.toStudentRealIdFromEmail()
                                if (realStudentId != null)
                                    info = tryGet { api.getStudentIdInfo(realStudentId).body() }
                            }
                            if (info != null && info.programId != null) {
                                user.publicInfo = info.toStudentInfo().toPublicInfo(
                                    cgpa = user.publicInfo.cgpa,
                                    totalCompletedCredit = user.publicInfo.totalCompletedCredit
                                )
                                firestore.addMyProgram(user.publicInfo)
                                if(user.publicInfo.nm.isNotEmpty())
                                    docRef.update("publicInfo", user.publicInfo)
                            }
                        }
                        if(user.privateInfo.sex.isNullOrEmpty()) {
                            val newPrivateInfo = tryGet {
                                api.getPrivateInfo(user.token).body()?.toPrivateInfo()
                            }?.toPrivateInfoExtended(
                                fbId = user.privateInfo.fbId,
                                fbLink = user.privateInfo.fbLink,
                                pic = user.privateInfo.pic,
                                ip = user.privateInfo.ip,
                                loc = user.privateInfo.loc
                            )
                            newPrivateInfo?.let {
                                user.privateInfo = it
                                docRef.update("privateInfo", newPrivateInfo)
                            }
                            pref.edit {
                                putString("nm", user.publicInfo.nm)
                                putString("proShortName", user.publicInfo.progShortName)
                            }
                        }
                        success.invoke(user)
                    }
                } else { failed.invoke("User Doesn't Exist") }
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> failed.invoke(it1) }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun getOrCreateUserProfileInfo(
        id: String,
        programId: String?,
        success: (Msg) -> Unit,
        loading: (msg: String) -> Unit,
        failed: (msg: String) -> Unit,
    ) {
        loading.invoke("Verifying ID- $id")
        val docRef = firestore.collection("userProfile").document(id)
        var loadFromPortal=true
        docRef.get()
            .addOnSuccessListener { document ->
                val profile = document.toObject<UserProfile>()
                if (profile != null) {
                    Log.d("getOrCreateUserProfileInfo", "DocumentSnapshot data: ${document.data}")
                    if(profile.publicInfo.cgpa == 0.0 || profile.publicInfo.nm.isEmpty() || profile.publicInfo.programId.isEmpty()) {
                        loading.invoke("Server Error. Couldn't Calculate CGPA for ID- $id. Retrying..")
                    } else {
                        val msgDis = "ID: ${profile.publicInfo.id}      CGPA: ${profile.publicInfo.cgpa}"
                        loadFromPortal = false
                        loading.invoke("Loaded Result From Backup Server For ID- $id.")
                        val shortProfile = Msg(
                            id = id,
                            nm = profile.publicInfo.nm,
                            msg = msgDis,
                            pic = profile.privateInfo.pic,
                            time = profile.lastUpdatedResultInfo)
                        //searchData.add(0,shortProfile)
                        success.invoke(shortProfile)
                    }
                }
                if(loadFromPortal) {
                    Log.d("getOrCreateUserProfileInfo", "No user found. Creating One")
//                    searchJob?.cancel()
//                    searchJob =
                    GlobalScope.launch(Dispatchers.IO){
                        try {
                            var info = api.getStudentIdInfo(id).body()
                            var realStudentId: String? = null
                            val realIds = listOf(
                                id.toStudentRealId(programId, 3),
                                id.toStudentRealId(programId, 4),
                                id.toStudentRealId(programId, 5),
                            )
                            for (realId in realIds) {
                                if (info?.programId.isNullOrEmpty())
                                    info = tryGet { realId?.let { api.getStudentIdInfo(it) }?.body() }
                                 else {
                                    realStudentId = realId
                                    break
                                }
                            }
                            val studentInfo = info?.toStudentInfo()
                            Log.d("getStudentInfo", "publicInfo: $studentInfo")
                            if(studentInfo != null && !info.programId.isNullOrEmpty())
                            {
                                firestore.addMyProgram(studentInfo.toPublicInfo())
                                loading.invoke("ID- ${studentInfo.studentId} Is Valid. Getting CGPA Info..")
                                getCgpa(
                                    id = studentInfo.studentId,
                                    api = api,
                                    semesterList = getSemesterList(studentInfo.firstSemId.toInt()),
                                    loading = {
                                        when (it) {
                                            -1 -> failed.invoke("Oops, Something Went Wrong.")
//                                            -2 -> failed.invoke("Couldn't Reach Server.")
                                            else -> loading.invoke("Getting SGPA For $id. loading Done For Semester $it .")
                                        } },
                                    success = { cgpa, totalCompletedCredit, fullResultInfo ->
                                        val msgDis: String
                                        if(cgpa!=0.0) {
                                            msgDis = "ID: ${studentInfo.studentId}      CGPA: $cgpa"
                                        } else {
                                            msgDis = "ID: ${studentInfo.studentId}"
                                            loading.invoke("Server Error. Couldn't Get All Semester SGPA For - $id.")
                                        }
                                        val publicInfo = studentInfo.toPublicInfo(
                                            cgpa = cgpa,
                                            totalCompletedCredit = totalCompletedCredit
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
                                        val updates = mapOf(
                                            "publicInfo" to publicInfo,
                                            "fullResultInfo" to fullResultInfo,
                                            "lastUpdatedResultInfo" to System.currentTimeMillis()
                                        )
                                        if (!document.exists()) {
                                            docRef.set(updates).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    firestore.collection("public")
                                                        .document("info")
                                                        .update("profileCount", FieldValue.increment(1))
                                                    loading.invoke("Info for $id Added To Backup Server.")
                                                } else {
                                                    loading.invoke("Firebase Server Error. Couldn't Save Info For $id To Backup Server.")
                                                }
                                            }
                                        } else {
                                            docRef.update(updates)
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
                val updates = mapOf(
                    "paymentInfo" to paymentInfoNew,
                    "lastUpdatedPaymentInfo" to System.currentTimeMillis()
                )
                docRef.update(updates)
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
        userProfile: UserProfile,
        success: (List<CourseInfo>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(userProfile.publicInfo.id)
        try {
//            val semInfo =api.getAllSemesterInfo(accessToken)
            val semInfo = if(userProfile.clearanceInfo.isEmpty())
                api.getClearanceInfo(userProfile.token).orEmpty().map { it.toClearanceInfo() }.reversed()
            else
                userProfile.clearanceInfo.reversed()
            var regCourseInfoNew = emptyList<CourseInfo>()
            run lit@{
                semInfo.forEach {lsInfo ->
                    val result = api.getRegisteredCourse(lsInfo.semesterId, userProfile.token).body()?.map { it.toCourseInfo() }
                    result?.let {
                        regCourseInfoNew = result
                    }
                    if(regCourseInfoNew.isNotEmpty())
                        return@lit
                }
            }
            Log.d("getStudentInfo", "registeredCourse: $regCourseInfoNew")
            if (userProfile.regCourseInfo notEqualsIgnoreOrder regCourseInfoNew && regCourseInfoNew.isNotEmpty()){
                success.invoke(regCourseInfoNew)
                val updates = mapOf(
                    "regCourseInfo" to regCourseInfoNew,
                    "lastUpdatedRegCourseInfo" to System.currentTimeMillis()
                )
                docRef.update(updates)
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
        userProfile: UserProfile,
        success: (List<LiveResultInfo>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val docRef = firestore.collection("userProfile").document(userProfile.publicInfo.id)
        val liveResultInfoListNew = mutableListOf<LiveResultInfo>()
        try {
            val semInfo = if(userProfile.clearanceInfo.isEmpty())
                api.getClearanceInfo(userProfile.token).orEmpty().map { it.toClearanceInfo() }.reversed()
            else
                userProfile.clearanceInfo.reversed()
            var registeredCourse = emptyList<CourseInfo>()
            run lit@{
                semInfo.forEach {lsInfo ->
                    val result = api.getRegisteredCourse(lsInfo.semesterId, userProfile.token).body()?.map { it.toCourseInfo() }
                    result?.let {
                        registeredCourse = result
                    }
                    if(registeredCourse.isNotEmpty())
                        return@lit
                }
            }
            Log.d("getStudentInfo", "registeredCourse: $registeredCourse")
            registeredCourse.forEach {
                val result = api.getLiveResult(it.courseSectionId!!, userProfile.token)
                val data = result.body()
                if(result.isSuccessful && data != null)
                    liveResultInfoListNew.add(
                        data.toLiveResultInfo(it.customCourseId!!, it.courseTitle!!, it.toShortSemName())
                    )
            }
            if(userProfile.liveResultInfo notEqualsIgnoreOrder liveResultInfoListNew && liveResultInfoListNew.isNotEmpty()){
                success.invoke(liveResultInfoListNew)
                val updates = mapOf(
                    "liveResultInfo" to liveResultInfoListNew,
                    "lastUpdatedLiveResultInfo" to System.currentTimeMillis()
                )
                docRef.update(updates)
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
        if(publicInfo.id.isEmpty()) return
        val docRef = firestore.collection("userProfile").document(publicInfo.id)
        getCgpa(
            id = publicInfo.id,
            api = api,
            semesterList = getSemesterList(publicInfo.firstSemId),
            loading = {
                when (it) {
                    -1 -> failed.invoke("Oops, something went wrong.")
//                    -2 -> failed.invoke("Couldn't reach server.")
                    else -> loading.invoke("Semester $it result loaded.")
                }
            },
            success = { cgpa, totalCompletedCredit, fullResultInfoListNew ->
                Log.d("TAG", "updateFullResultInfo: $cgpa")

                if(fullResultInfoListNew.size >= fullResultInfoList.size){
                    if((fullResultInfoList notEqualsIgnoreOrder fullResultInfoListNew && fullResultInfoListNew.isNotEmpty()) || (publicInfo.cgpa!=cgpa && cgpa!=0.0)){
                        success.invoke(fullResultInfoListNew, cgpa, totalCompletedCredit)
                        val updates = mapOf(
                            "fullResultInfo" to fullResultInfoListNew,
                            "publicInfo" to publicInfo.copy(cgpa = cgpa, totalCompletedCredit = totalCompletedCredit),
                            "lastUpdatedResultInfo" to System.currentTimeMillis()
                        )
                        docRef.update(updates)
                    } else failed.invoke("No new data found.")
                } else failed.invoke("No new data found.")

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
                val updates = mapOf(
                    "clearanceInfo" to clearanceInfoNew,
                    "lastUpdatedClearanceInfo" to System.currentTimeMillis()
                )
                docRef.update(updates)
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

    override suspend fun syncPrograms(): List<ProgramInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val programRef = firestore.collection("program").document("list")

                val newList = tryGet { api.getAdmissionInfo() }?.body()?.data?.flatMap { data ->
                    data.admissionCircularPrograms.map { it.toProgramInfo() }
                }.orEmpty()
                val oldList = tryGet { programRef.get().await() }
                    ?.toObject<ProgramList>()?.list
                    ?: pref.readObject<List<ProgramInfo>>(PrefKeys.PROGRAM_LIST).orEmpty()
                val combinedList  = (newList + oldList).distinctBy { it.programId }.sortedBy { it.shortName }
                val isOldSorted = oldList == oldList.sortedBy { it.programId }

                if(combinedList .size > oldList.size || !isOldSorted){
                    programRef.set(ProgramList(combinedList )).await()
                    pref.saveObject(PrefKeys.PROGRAM_LIST, combinedList )
                    combinedList
                } else oldList
            }catch (e: Exception) {
                e.log("syncPrograms")
                pref.readObject<List<ProgramInfo>>(PrefKeys.PROGRAM_LIST).orEmpty()
            }
        }
    }
}