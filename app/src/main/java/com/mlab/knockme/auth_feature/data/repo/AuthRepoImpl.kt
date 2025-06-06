package com.mlab.knockme.auth_feature.data.repo

import android.app.Activity
import android.credentials.GetCredentialException
import android.util.Log
import androidx.core.content.edit
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.mlab.knockme.R
import com.mlab.knockme.auth_feature.data.data_source.LoginInformation
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.domain.model.*
import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import com.mlab.knockme.core.util.*
import com.mlab.knockme.pref
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.roundToInt


class AuthRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val api: PortalApi,
    private val firestore: FirebaseFirestore,
    private val cloudStore: FirebaseStorage
) : AuthRepo {
    private lateinit var myRef: DocumentReference

    override fun isUserAuthenticatedInFirebase(): Boolean {
        return auth.currentUser != null
    }

    override fun getFirebaseAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }

    }

    override fun firebaseSignIn(credential: Any, success: () -> Unit, failed: () -> Unit) {
        val firebaseCredential = when (credential) {
            is AccessToken -> FacebookAuthProvider.getCredential(credential.token)
            is GoogleIdTokenCredential -> GoogleAuthProvider.getCredential(credential.idToken, null)
            else -> {
                failed.invoke(); return
            }
        }
        auth.signInWithCredential(firebaseCredential)
            .addOnSuccessListener {
                Log.d("TAG1", "firebase:onSuccess:${it.user}")
                success.invoke()
            }
            .addOnFailureListener {
                Log.d("TAG1", "firebase:onFailed:${it.localizedMessage}")
                failed.invoke()
            }
    }

    override suspend fun googleSignIn(activity: Activity): Resource<GoogleIdTokenCredential> {
        return withContext(Dispatchers.IO) {
            val signInGoogleOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(activity.getString(R.string.knock_me_web_client_id))
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInGoogleOption)
                .build()
            try {
                val result = CredentialManager
                    .create(activity)
                    .getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    //google sign in success
                    val photoUrl = googleCredential
                        .profilePictureUri?.toString()
                        ?.replace("s96-c", "s480-c")
                    pref.edit {
                        putString("userId", googleCredential.id)
                        putString("name", googleCredential.displayName.orEmpty())
                        putString("fbLink", "")
                        putString("pic", photoUrl)
                    }
                    Resource.Success(googleCredential)
                } else
                    Resource.Error("Only Google Account Allowed")
            } catch (e: GetCredentialCancellationException) {
                e.printStackTrace()
                if (e.type == GetCredentialException.TYPE_USER_CANCELED)
                    Resource.Error("Canceled by user.", code = 20)
                else
                    Resource.Error(e.message ?: "Google Sign In Error!")
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error(e.localizedMessage ?: "Google Sign In Error!")
            }
        }
    }

    override fun fbLogin(
        buttonFacebookLogin: LoginButton,
        callbackManager: CallbackManager,
        success: (data: SocialAuthInfo) -> Unit,
        failed: () -> Unit
    ) {
        buttonFacebookLogin.permissions = listOf("public_profile", "user_link")//, "link"

        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d("TAG1", "facebook:onSuccess:${result.accessToken} $result ")
                val dataToken = SocialAuthInfo(result.accessToken)
                getData(dataToken) { data ->
                    Log.d("fbLink.", "facebook:onSuccess:${data.userId} ${data.fbLink}")
                    pref.edit {
                        putString("userId", data.userId)
                        putString("name", data.name)
                        putString("fbLink", data.fbLink)
                        putString("pic", data.pic)
                    }
                    success.invoke(data)
                }
                // loginViewModel.signIn(result.accessToken)
            }

            override fun onCancel() {
                Log.d("TAG2", "facebook:onCancel")
                failed.invoke()
            }

            override fun onError(error: FacebookException) {
                Log.d("TAG3", "facebook:onError", error)
                failed.invoke()
            }
        })
    }

    override fun getStudentIdInfo(id: String): Flow<Resource<StudentInfo>> = flow {
        emit(Resource.Loading("Getting ID Info.."))
        try {
            val result = api.getStudentIdInfo(id)
            val info = result.body()?.toStudentInfo()
            if (result.isSuccessful && info != null) {
                emit(Resource.Success(info))
            }
            emit(Resource.Loading("Getting ID Info.."))
            Log.d("TAG", "getStudentIdInfo: $info")
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
            Log.d("TAG", "getStudentIdInfo: ${e.message}")

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun getStudentInfo(
        id: String,
        pass: String,
        socialAuthInfo: SocialAuthInfo
    ): Flow<Resource<UserProfile>> = channelFlow {
        myRef = firestore.collection("userProfile").document(id)

        send(Resource.Loading("Getting ID Info.."))
        var publicInfo = StudentInfo()
        //myRef.update("regions", FieldValue("greater_virginia"))
        myRef.get().addOnSuccessListener { document ->
            val user = document.toObject<UserProfile>()
            if (user != null && (user.privateInfo.fbId == socialAuthInfo.userId || user.privateInfo.fbId == id)) {
                Log.d("OnSuccessListener", "Login Success for: ${document.data}")
                GlobalScope.launch(Dispatchers.IO) {
                    if (user.publicInfo.programId.isEmpty()){
                        var info = tryGet { api.getStudentIdInfo(id).body() }
                        if ((info == null || info.studentId.isNullOrEmpty()) && user.privateInfo.email != null) {
                            val realStudentId = user.privateInfo.email!!.toStudentRealIdFromEmail()
                            if (realStudentId != null)
                                info = tryGet { api.getStudentIdInfo(realStudentId).body() }
                        }
                        if (info != null && info.programId != null) {
                            publicInfo = info.toStudentInfo()
                            firestore.addMyProgram(publicInfo.toPublicInfo())
                            user.publicInfo = publicInfo.toPublicInfo(
                                cgpa = user.publicInfo.cgpa,
                                totalCompletedCredit = user.publicInfo.totalCompletedCredit
                            )
                            if(user.publicInfo.nm.isNotEmpty())
                                myRef.update("publicInfo", user.publicInfo)
                        }
                    }
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
                        myRef.update("privateInfo", newPrivateInfo)
                    }
                    pref.edit {
                        putString("nm", user.publicInfo.nm)
                        putString("proShortName", user.publicInfo.progShortName)
                        putString("studentId", publicInfo.studentId.ifEmpty { id })
                    }
                    pref.saveObject(PrefKeys.USER_INFO, user)
                    send(Resource.Success(user))
                    this.cancel()
                }
            } else if (!document.exists()) {
                Log.d("OnSuccessListener", "No user found. Creating one. ${document.data}")
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        send(Resource.Loading("Getting Auth Info.."))
                        Log.d("getStudentInfo", "id pass: $id $pass")
                        val authInfo = api.getAuthInfo(LoginInformation(id, pass)).body()
                        if (authInfo == null || authInfo.message == "failed") {
                            send(Resource.Error("Invalid Portal Password"))
                            Log.d("TAG", "getStudentInfo: Invalid Portal Password")
                            this.cancel()
                            return@launch
                        }
                        Log.d("getStudentInfo", "authInfo: $authInfo")
                        send(Resource.Loading("Getting Program Info.."))
                        val privateInfo =
                            tryGet { api.getPrivateInfo(authInfo.accessToken).body()?.toPrivateInfo() }
                        Log.d("getStudentInfo", "privateInfo: $privateInfo")
                        send(Resource.Loading("Getting Clearace Info.."))
                        val clearanceInfo = tryGet {
                            api.getClearanceInfo(authInfo.accessToken)?.map { it.toClearanceInfo() }
                        }
                        Log.d("getStudentInfo", "clearanceInfo: $clearanceInfo")
                        send(Resource.Loading("Getting lastSemester Info.."))
                        val semInfo = clearanceInfo.orEmpty().reversed()
                        var registeredCourse = emptyList<CourseInfo>()
                        run lit@{
                            semInfo.forEach { lsInfo ->
                                send(Resource.Loading("Getting registeredCourse Info.."))
                                val result = tryGet {
                                    api.getRegisteredCourse(lsInfo.semesterId, authInfo.accessToken)
                                        .body()
                                        ?.map { it.toCourseInfo() }
                                }
                                result?.let {
                                    registeredCourse = result
                                }
                                if (registeredCourse.isNotEmpty())
                                    return@lit
                            }
                        }
                        Log.d("getStudentInfo", "registeredCourse: $registeredCourse")
                        val liveResultInfoList = mutableListOf<LiveResultInfo>()
                        registeredCourse.forEachIndexed { i, it ->
                            send(Resource.Loading("Getting Live Result (${i + 1}/${registeredCourse.size})"))
                            val result = tryGet {
                                api.getLiveResult(
                                    courseSectionId = it.courseSectionId!!,
                                    accessToken = authInfo.accessToken
                                )
                            }
                            val data = result?.body()
                            if (result?.isSuccessful == true && data != null)
                                liveResultInfoList.add(
                                    data.toLiveResultInfo(
                                        customCourseId = it.customCourseId!!,
                                        courseTitle = it.courseTitle!!,
                                        shortSemName = it.toShortSemName()
                                    )
                                )
                        }
                        Log.d("getStudentInfo", "liveResultInfoList: $liveResultInfoList")
                        send(Resource.Loading("Getting payment Info.."))
                        val paymentInfo =
                            tryGet { api.getPaymentInfo(authInfo.accessToken)?.toPaymentInfo() }
                        Log.d("getStudentInfo", "paymentInfo: $paymentInfo")
                        send(Resource.Loading("Getting location Info.."))
                        val locationInfo = tryGet { api.getLocationInfo()?.toLocationInfo() }
                        Log.d("getStudentInfo", "locationInfo: $locationInfo")
                        val time = System.currentTimeMillis()
                        val userProfile =
                            UserProfile(
                                token = authInfo.accessToken,
                                lastUpdatedPaymentInfo = time,
                                lastUpdatedRegCourseInfo = time,
                                lastUpdatedLiveResultInfo = time,
                                lastUpdatedResultInfo = time,
                                lastUpdatedClearanceInfo = time,
//                                publicInfo = PublicInfo(
//                                    id = id,
//                                    nm = publicInfo.studentName!!,
//                                    progShortName = publicInfo.progShortName!!,
//                                    batchNo = publicInfo.batchNo!!,
//                                    cgpa = cgpa,
//                                    totalCompletedCredit = totalCompletedCredit,
//                                    firstSemId = publicInfo.firstSemId!!.toInt()
//                                ),
                                privateInfo = PrivateInfoExtended(
                                    fbId = socialAuthInfo.userId,
                                    fbLink = socialAuthInfo.fbLink,
                                    pic = socialAuthInfo.pic,
                                    bloodGroup = privateInfo?.bloodGroup,
                                    email = privateInfo?.email,
                                    permanentHouse = privateInfo?.permanentHouse,
                                    ip = locationInfo?.ip.orEmpty(),
                                    loc = locationInfo?.loc.orEmpty()
                                ),
                                paymentInfo = paymentInfo ?: PaymentInfo(),
                                regCourseInfo = registeredCourse,
                                liveResultInfo = liveResultInfoList,
//                                fullResultInfo = fullResultInfo,
                                clearanceInfo = clearanceInfo.orEmpty()
                            )

                        Log.d("TAG", "getStudentInfoFinal: $userProfile")

                        try {
                            var info = tryGet { api.getStudentIdInfo(id).body() }
                            if ((info == null || info.studentId.isNullOrEmpty()) && privateInfo?.email != null) {
                                val realStudentId = privateInfo.email.toStudentRealIdFromEmail()
                                if (realStudentId != null)
                                    info = tryGet { api.getStudentIdInfo(realStudentId).body() }
                            }
                            if (info != null && info.programId != null) {
                                publicInfo = info.toStudentInfo()
                                firestore.addMyProgram(publicInfo.toPublicInfo())
                            } else {
                                send(Resource.Error("Invalid Student ID"))
                                Log.d("TAG", "getStudentIdInfo: Invalid Student ID")
                                this.cancel()
                            }
                            Log.d("getStudentInfo", "publicInfo: $publicInfo")
                        } catch (e: HttpException) {
                            send(Resource.Error("Invalid Student ID"))
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}}")
                            this.cancel()
                        } catch (e: EOFException) {
                            send(Resource.Error("Student Portal Server Error."))
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                            this.cancel()
                        } catch (e: IOException) {
                            send(Resource.Error("Couldn't reach server."))
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                            this.cancel()
                        } catch (e: Exception) {
                            send(Resource.Error("Server error."))
                            Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                            this.cancel()
                        }

                        if (publicInfo.firstSemId.isNotEmpty()) {
                            send(Resource.Loading("Getting CGPA Info.."))
                            getCgpa(
                                id = publicInfo.studentId.ifEmpty { id },
                                api = api,
                                semesterList = getSemesterList(
                                    publicInfo.firstSemId.toIntOrNull() ?: 0
                                ),
                                useSuccessOnly = true,
                                loading = {
                                    send(Resource.Loading("Semester $it result loaded."))
                                }, success = { cgpa, totalCompletedCredit, fullResultInfo ->
                                    if (cgpa == 0.0)
                                        send(Resource.Loading("Server Error, CGPA can't be calculated"))
                                    else
                                        send(Resource.Loading("ID: $id, CGPA: $cgpa"))
                                    myRef.set(userProfile.copy(
                                        publicInfo = publicInfo.toPublicInfo(
                                            cgpa = cgpa,
                                            totalCompletedCredit = totalCompletedCredit
                                        ).apply {
                                            if (nm.isEmpty())
                                                nm = userProfile.privateInfo.name ?: socialAuthInfo.name
                                            if (this.id.isEmpty())
                                                this.id = userProfile.privateInfo.email?.toStudentRealIdFromEmail().orEmpty()
                                        },
                                        fullResultInfo = fullResultInfo
                                    )).addOnCompleteListener {
                                        GlobalScope.launch(Dispatchers.IO) {
                                            if (it.isSuccessful) {
                                                firestore
                                                    .collection("public")
                                                    .document("info")
                                                    .update("profileCount", FieldValue.increment(1))
                                                pref.edit {
                                                    putString("nm", userProfile.publicInfo.nm)
                                                    putString("proShortName", userProfile.publicInfo.progShortName)
                                                    putString("studentId", publicInfo.studentId.ifEmpty { id })
                                                }
                                                pref.saveObject(PrefKeys.USER_INFO, userProfile)
                                                send(Resource.Success(userProfile))
                                            } else
                                                send(Resource.Error("Firebase Server Error.", userProfile))
                                        }
                                    }
                                })
                        } else
                            send(Resource.Error("Invalid Student ID."))

                        Log.d("TAG", "getStudentIdInfo: $publicInfo")

//                        val ref = cloudStore.reference.child("users/${id}/dp.jpg")
//                        val uploadTask = ref
//                            .putStream(
//                                api.getImgByteStream(socialAuthInfo.pic)
//                                    .byteStream()
//                            )
//                        uploadTask.continueWithTask { task ->
//                            if (!task.isSuccessful) {
//                                task.exception?.let { throw it }
//                            }
//                            ref.downloadUrl
//                        }.addOnCompleteListener { task ->
//                            val fbPic : String =
//                                if (task.isSuccessful)
//                                    task.result.toString()
//                                else
//                                    socialAuthInfo.pic
//                            //Log.d("TAG", "getStudentInfoFinal: $userProfileMap")
//                        }
                    } catch (e: HttpException) {
                        send(Resource.Error("Server not responding."))
                        Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        this.cancel()
                    } catch (e: EOFException) {
                        send(Resource.Error("Student portal server error."))
                        Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        this.cancel()
                    } catch (e: IOException) {
                        send(Resource.Error("Couldn't reach server."))
                        Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        this.cancel()
                    } catch (e: Exception) {
                        send(Resource.Error("Server error."))
                        Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                        this.cancel()
                    }
                }

            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        send(Resource.Loading("Getting Auth Info.."))
                        Log.d("getStudentInfo", "id pass: $id $pass")
                        val authInfo = api.getAuthInfo(LoginInformation(id, pass)).body()
                        Log.d("getStudentInfo", "authInfo: $authInfo")
                        if (authInfo == null || authInfo.message == "failed") {
                            send(Resource.Error("Invalid Portal Password"))
                            Log.d("TAG", "getStudentInfo: Invalid Portal Password")
                            this.cancel()
                            return@launch
                        }
                        send(Resource.Loading("Getting Private Info.."))
                        val privateInfo =
                            tryGet { api.getPrivateInfo(authInfo.accessToken).body()?.toPrivateInfo() }
                        Log.d("getStudentInfo", "privateInfo: $privateInfo")
                        send(Resource.Loading("Getting Clearance Info.."))
                        val clearanceInfo = tryGet {
                            api.getClearanceInfo(authInfo.accessToken)?.map { it.toClearanceInfo() }
                        }
                        Log.d("getStudentInfo", "clearanceInfo: $clearanceInfo")
                        send(Resource.Loading("Getting lastSemester Info.."))
                        val semInfo = clearanceInfo.orEmpty().reversed()
                        var registeredCourse = emptyList<CourseInfo>()
                        run lit@{
                            semInfo.forEach { lsInfo ->
                                send(Resource.Loading("Getting registeredCourse Info.."))
                                val result = tryGet {
                                    api.getRegisteredCourse(lsInfo.semesterId, authInfo.accessToken)
                                        .body()
                                        ?.map { it.toCourseInfo() }
                                }
                                result?.let {
                                    registeredCourse = result
                                }
                                if (registeredCourse.isNotEmpty())
                                    return@lit
                            }
                        }
                        Log.d("getStudentInfo", "registeredCourse: $registeredCourse")
                        val liveResultInfoList = mutableListOf<LiveResultInfo>()
                        registeredCourse.forEachIndexed { i, it ->
                            send(Resource.Loading("Getting Live Result (${i + 1}/${registeredCourse.size})"))
                            val result = tryGet {
                                api.getLiveResult(
                                    courseSectionId = it.courseSectionId!!,
                                    accessToken = authInfo.accessToken
                                )
                            }
                            val data = result?.body()
                            if (result?.isSuccessful == true && data != null)
                                liveResultInfoList.add(
                                    data.toLiveResultInfo(
                                        customCourseId = it.customCourseId!!,
                                        courseTitle = it.courseTitle!!,
                                        shortSemName = it.toShortSemName()
                                    )
                                )
                        }
                        Log.d("getStudentInfo", "liveResultInfoList: $liveResultInfoList")
                        send(Resource.Loading("Getting payment Info.."))
                        val paymentInfo =
                            tryGet { api.getPaymentInfo(authInfo.accessToken)?.toPaymentInfo() }
                        Log.d("getStudentInfo", "paymentInfo: $paymentInfo")
                        send(Resource.Loading("Getting location Info.."))
                        val locationInfo = tryGet { api.getLocationInfo()?.toLocationInfo() }
                        Log.d("getStudentInfo", "locationInfo: $locationInfo")
                        //authInfo.accessToken
                        val privateInfoE = privateInfo.toPrivateInfoExtended(
                            fbId = socialAuthInfo.userId,
                            fbLink = socialAuthInfo.fbLink,
                            pic = socialAuthInfo.pic,
                            ip = locationInfo?.ip.orEmpty(),
                            loc = locationInfo?.loc.orEmpty()
                        )
                        //paymentInfo //registeredCourse //liveResultInfoList
                        val updates = mapOf(
                            "token" to authInfo.accessToken,
                            "privateInfo" to privateInfoE,
                            "registeredCourse" to registeredCourse,
                            "liveResultInfoList" to liveResultInfoList
                        )

                        myRef.update(updates)


                        Log.d("TAG", "getStudentInfoFinal: $privateInfo")
                        pref.edit {
                            putString("nm", authInfo.name)
                            putString("proShortName", publicInfo.progShortName)
                            putString("studentId", publicInfo.studentId.ifEmpty { id })
                        }
                        send(Resource.Success(UserProfile(token = authInfo.accessToken)))
                    } catch (e: HttpException) {
                        send(Resource.Error("Server not responding."))
                        Log.d("TAG", "getStudentInfo: ${e.message} ${e.localizedMessage}")
                        this.cancel()
                    } catch (e: EOFException) {
                        send(Resource.Error("Student portal server error."))
                        Log.d("TAG", "getStudentInfo: ${e.message} ${e.localizedMessage}")
                        this.cancel()
                    } catch (e: IOException) {
                        send(Resource.Error("Couldn't reach server."))
                        Log.d("TAG", "getStudentInfo: ${e.message} ${e.localizedMessage}")
                        this.cancel()
                    } catch (e: Exception) {
                        send(Resource.Error("Server error."))
                        Log.d("TAG", "getStudentInfo: ${e.message} ${e.localizedMessage}")
                        this.cancel()
                    }
                    Log.d("TAG", "getStudentIdInfo: $publicInfo")
                }
            }
        }
            .addOnFailureListener { exception ->
                Log.d("OnFailListener", "get failed with ", exception)
            }

        awaitClose()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun getCgpaX(
        id: String,
        semesterList: List<String>,
        loading: (index: Int) -> Unit,
        success: (cgpa: Double, fullResultInfo: List<FullResultInfo>) -> Unit
    ) {
        if (semesterList.isEmpty()) {
            success.invoke(0.0, emptyList())
        }
        var weightedCgpa = 0.0
        var totalCreditWeight = 0.0
        var resultFound = 0
        val fullResultInfo = arrayListOf<FullResultInfo>()
        semesterList.forEachIndexed { i, semesterId ->
            GlobalScope.launch(Dispatchers.IO) {
                val semesterResultInfo = FullResultInfo()
                try {
                    delay(i * 100.toLong())
                    val resultInfo = api.getResultInfo(semesterId, id).body()
                    Log.d("TAG", "getCgpa $semesterId resultInfo: $resultInfo")
                    if (!resultInfo.isNullOrEmpty()) {
                        loading.invoke(semesterList.size - i)
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
                        if (resultInfo[0].cgpa != 0.0)
                            semesterResultInfo.semesterInfo =
                                resultInfo[0].toSemesterInfo(creditTaken)
                        else
                            semesterResultInfo.semesterInfo =
                                resultInfo[1].toSemesterInfo(creditTaken)
                        fullResultInfo.add(semesterResultInfo)  //adding
                    }
                    resultFound++
                    if (resultFound == semesterList.size) {
                        var cgpa = weightedCgpa / totalCreditWeight
                        cgpa = if (!cgpa.isNaN()) (cgpa * 100.0).roundToInt() / 100.0 else 0.0

                        fullResultInfo.sortBy { it.semesterInfo.semesterId }
                        success.invoke(cgpa, fullResultInfo)
                    }
                } catch (e: HttpException) {
                    loading.invoke(-1)
                    Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                    this.cancel()

                } catch (e: EOFException) {
                    success.invoke(0.0, arrayListOf())
                    Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                    this.cancel()
                } catch (e: IOException) {
                    loading.invoke(-2)
                    Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                    this.cancel()
                }
                //this.cancel()
            }
        }
    }

    private fun getSemesterListX(firstSemId: Int): List<String> {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val endYearSemesterCount =
            if (month < 3) 0
            else if (month < 7) 1
            else if (month < 11) 2
            else 3

        val yearEnd = year % 100
        //val initial = id.slice(0..2).toInt()  //.split('-')[0].toInt()
        val yr: Int = firstSemId / 10
        var semester: Int = firstSemId % 10
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


    private fun getData(data: SocialAuthInfo, success: (data: SocialAuthInfo) -> Unit) {
        val graphRequests = GraphRequestBatch(
            GraphRequest.newGraphPathRequest(
                data.accessToken,
                "/me?fields=id,link"
            ) {
                Log.d("req1", "facebook:onSuccess:${it.jsonObject}")
                data.userId = it.jsonObject?.getString("id").orEmpty()
                data.fbLink = it.jsonObject?.getString("link").orEmpty()
                data.name = it.jsonObject?.getString("name").orEmpty()
            },
            GraphRequest.newGraphPathRequest(
                data.accessToken,
                "/me/picture?width=480&height=480&redirect=false"
            ) {
                Log.d("req2", "facebook:onSuccess:${it.jsonObject}")
                data.pic = it.jsonObject?.getJSONObject("data")?.getString("url").toString()
            })
        graphRequests.addCallback {
            success.invoke(data)
            Log.d("addCallback", "getData: hii")
        }
        graphRequests.executeAsync()
    }
}
