package com.mlab.knockme.auth_feature.data.repo

import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.GraphRequestBatch
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.mlab.knockme.auth_feature.data.data_source.LoginInformation
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.domain.model.FBResponse
import com.mlab.knockme.auth_feature.domain.model.FullResultInfo
import com.mlab.knockme.auth_feature.domain.model.LiveResultInfo
import com.mlab.knockme.auth_feature.domain.model.PrivateInfoExtended
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import com.mlab.knockme.auth_feature.domain.model.ResultInfo
import com.mlab.knockme.auth_feature.domain.model.StudentInfo
import com.mlab.knockme.auth_feature.domain.model.UserProfile
import com.mlab.knockme.auth_feature.domain.repo.AuthRepo
import com.mlab.knockme.auth_feature.util.SignResponse
import com.mlab.knockme.core.util.Resource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.roundToInt


class AuthRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val api: PortalApi,
    private val firestore: FirebaseFirestore
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

    override fun firebaseSignIn(token: AccessToken, success: () -> Unit, failed: () -> Unit) {
        auth.signInWithCredential(FacebookAuthProvider.getCredential(token.token))
            .addOnSuccessListener {
                Log.d("TAG1", "firebase:onSuccess:${it.user}")
                success.invoke()
            }
            .addOnFailureListener {
                Log.d("TAG1", "firebase:onFailed:${it.localizedMessage}")
                failed.invoke()
            }
    }

    override suspend fun firebaseSignOut(): StateFlow<SignResponse<Boolean>> {
        val mutableStateFlow: MutableStateFlow<SignResponse<Boolean>> =
            MutableStateFlow(SignResponse.Loading)
        coroutineScope {
            try {
                auth.signOut()
                mutableStateFlow.emit(SignResponse.Success(true))
            } catch (e: Exception) {
                mutableStateFlow.emit(SignResponse.Error(e.localizedMessage ?: "Sign Out Failed"))
            }
        }
        return mutableStateFlow
    }

    override fun fbLogin(
        buttonFacebookLogin: LoginButton,
        callbackManager: CallbackManager,
        success: (data: FBResponse) -> Unit,
        failed: () -> Unit
    ) {
        buttonFacebookLogin.permissions = listOf("public_profile", "user_link")//, "link"

        buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d("TAG1", "facebook:onSuccess:${result.accessToken} $result ")
                val dataToken = FBResponse(result.accessToken)
                getData(dataToken) { data ->
                    Log.d("TAG2.", "facebook:onSuccess:${data.fbId} ${data.pic}")
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
            val info = api.getStudentIdInfo(id).toStudentInfo()
            emit(Resource.Loading("Getting ID Info.."))
            Log.d("TAG", "getStudentIdInfo: $info")
        } catch (e: HttpException) {
            emit(Resource.Error(e.message()))
            Log.d("TAG", "getStudentIdInfo: ${e.message()}")

        } catch (e: IOException) {
            emit(Resource.Error(e.message.toString()))
            Log.d("TAG", "getStudentIdInfo: ${e.message}")

        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun getStudentInfo(
        id: String,
        pass: String,
        fbInfo: FBResponse
    ): Flow<Resource<UserProfile>> = channelFlow {
        myRef = firestore.collection("userProfile").document(id)
        val mapper = GsonBuilder().serializeNulls().create()

        send(Resource.Loading("Getting ID Info.."))
        var publicInfo = StudentInfo()
        //myRef.update("regions", FieldValue("greater_virginia"))
        myRef.get().addOnSuccessListener { document ->
//            val city = document.toObject<UserProfile>()?.publicInfo?.fbId
//            val x =mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java).publicInfo.fbId
            if (document != null && document.exists() &&
                (((mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java).privateInfo.fbId) == fbInfo.fbId) ||
                        ((mapper.fromJson(mapper.toJson(document.data), UserProfile::class.java).privateInfo.fbId) == id))
            ) {
                Log.d("OnSuccessListener", "Login Success for: ${document.data}")
                GlobalScope.launch(Dispatchers.IO) {
                    send(
                        Resource.Success(
                            mapper.fromJson(
                                mapper.toJson(document.data),
                                UserProfile::class.java
                            )
                        )
                    )
                    this.cancel()
                }
            } else if (!document.exists()) {
                Log.d("OnSuccessListener", "No user found. Creating one. ${document.data}")
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        publicInfo = api.getStudentIdInfo(id).toStudentInfo()
                        Log.d("getStudentInfo", "publicInfo: $publicInfo")
                        send(Resource.Loading("Getting CGPA Info.."))
                    } catch (e: HttpException) {
                        send(Resource.Error("Oops, something went wrong."))
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
                    }
                    getCgpa(
                        id = id,
                        loading = {
                            GlobalScope.launch(Dispatchers.IO) {
                                when (it) {
                                    -1 -> send(Resource.Error("Oops, something went wrong."))
                                    -2 -> send(Resource.Error("Couldn't reach server."))
                                    else -> send(Resource.Loading("Semester $it result loaded."))
                                }
                            }
                        }) { cgpa, fullResultInfo ->
                        GlobalScope.launch(Dispatchers.IO) {
                            if (cgpa == 0.0)
                                send(Resource.Loading("Server Error, CGPA can't be calculated"))
                            else
                                send(Resource.Loading("ID: $id, CGPA: $cgpa"))
                            try {
                                send(Resource.Loading("Getting Auth Info.."))
                                Log.d("getStudentInfo", "id pass: $id $pass")
                                val authInfo = api.getAuthInfo(LoginInformation(id, pass))
                                Log.d("getStudentInfo", "authInfo: $authInfo")
                                send(Resource.Loading("Getting Private Info.."))
                                val privateInfo = api.getPrivateInfo(authInfo.accessToken).toPrivateInfo()
                                Log.d("getStudentInfo", "privateInfo: $privateInfo")
                                send(Resource.Loading("Getting lastSemester Info.."))
                                val lastSemesterId = api.getAllSemesterInfo(authInfo.accessToken)[0].semesterId
                                Log.d("getStudentInfo", "lastSemesterId: $lastSemesterId")
                                send(Resource.Loading("Getting registeredCourse Info.."))
                                val registeredCourse = api.getRegisteredCourse(lastSemesterId, authInfo.accessToken).map { it.toCourseInfo() }
                                Log.d("getStudentInfo", "registeredCourse: $registeredCourse")
                                send(Resource.Loading("Getting Live Result Info.."))
                                val liveResultInfoList = mutableListOf<LiveResultInfo>()
                                registeredCourse.onEach {
                                    liveResultInfoList.add(api.getLiveResult(it.courseSectionId!!, authInfo.accessToken)
                                        .toLiveResultInfo(lastSemesterId))
                                }
                                Log.d("getStudentInfo", "liveResultInfoList: $liveResultInfoList")
                                send(Resource.Loading("Getting payment Info.."))
                                val paymentInfo = api.getPaymentInfo(authInfo.accessToken).toPaymentInfo()
                                Log.d("getStudentInfo", "paymentInfo: $paymentInfo")
                                send(Resource.Loading("Getting location Info.."))
                                val locationInfo = api.getLocationInfo().toLocationInfo()
                                Log.d("getStudentInfo", "locationInfo: $locationInfo")
                                val userProfile =
                                    UserProfile(
                                        token = authInfo.accessToken,
                                        publicInfo = PublicInfo(
                                            id = id,
                                            nm = publicInfo.studentName!!,
                                            progShortName = publicInfo.progShortName!!,
                                            batchNo = publicInfo.batchNo!!,
                                            cgpa = cgpa,
                                            lastUpdated = System.currentTimeMillis()
                                        ),
                                        privateInfo = PrivateInfoExtended(
                                            fbId = fbInfo.fbId,
                                            fbLink = fbInfo.fbLink,
                                            pic = fbInfo.pic,
                                            bloodGroup = privateInfo.bloodGroup,
                                            email = privateInfo.email,
                                            permanentHouse = privateInfo.permanentHouse,
                                            ip = locationInfo.ip!!,
                                            loc = locationInfo.loc!!
                                        ),
                                        paymentInfo = paymentInfo,
                                        courseInfo = registeredCourse,
                                        liveResultInfo = liveResultInfoList,
                                        fullResultInfo = fullResultInfo
                                    )

                                Log.d("TAG", "getStudentInfoFinal: $userProfile")
                                //val userProfileMap=mapper.fromJson(mapper.toJson(userProfile), Map::class.java)
                                //Log.d("TAG", "getStudentInfoFinal: $userProfileMap")
                                myRef.set(userProfile).addOnCompleteListener {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        if (it.isSuccessful) {
                                            firestore
                                                .collection("public")
                                                .document("info")
                                                .update("profileCount", FieldValue.increment(1))
                                            send(Resource.Success(userProfile))
                                        } else
                                            send(
                                                Resource.Error("Firebase Server Error.", userProfile)
                                            )
                                    }
                                }


                            } catch (e: HttpException) {
                                send(Resource.Error("Oops, something went wrong."))
                                Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                                this.cancel()
                            } catch (e: EOFException) {
                                send(Resource.Error("Student Portal Server Error."))
                                Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                                this.cancel()
                            } catch (e: IOException) {
                                send(Resource.Error("Couldn't reach server."))
                                Log.d("TAG", "getStudentIdInfo: ${e.message} ${e.localizedMessage}")
                                this.cancel()
                            }
                        }
                    }
                    Log.d("TAG", "getStudentIdInfo: $publicInfo")
                }

            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        send(Resource.Loading("Getting Auth Info.."))
                        Log.d("getStudentInfo", "id pass: $id $pass")
                        val authInfo = api.getAuthInfo(LoginInformation(id, pass))
                        Log.d("getStudentInfo", "authInfo: $authInfo")
                        send(Resource.Loading("Getting Private Info.."))
                        val privateInfo = api.getPrivateInfo(authInfo.accessToken).toPrivateInfo()
                        Log.d("getStudentInfo", "privateInfo: $privateInfo")
                        send(Resource.Loading("Getting lastSemester Info.."))
                        val lastSemesterId = api.getAllSemesterInfo(authInfo.accessToken)[0].semesterId
                        Log.d("getStudentInfo", "lastSemesterId: $lastSemesterId")
                        send(Resource.Loading("Getting registeredCourse Info.."))
                        val registeredCourse = api.getRegisteredCourse(lastSemesterId, authInfo.accessToken).map { it.toCourseInfo() }
                        Log.d("getStudentInfo", "registeredCourse: $registeredCourse")
                        send(Resource.Loading("Getting Live Result Info.."))
                        val liveResultInfoList = mutableListOf<LiveResultInfo>()
                        registeredCourse.onEach {
                            liveResultInfoList.add(api
                                    .getLiveResult(it.courseSectionId!!, authInfo.accessToken)
                                    .toLiveResultInfo(lastSemesterId)
                                )
                        }
                        Log.d("getStudentInfo", "liveResultInfoList: $liveResultInfoList")
                        send(Resource.Loading("Getting payment Info.."))
                        val paymentInfo = api.getPaymentInfo(authInfo.accessToken).toPaymentInfo()
                        Log.d("getStudentInfo", "paymentInfo: $paymentInfo")
                        send(Resource.Loading("Getting location Info.."))
                        val locationInfo = api.getLocationInfo().toLocationInfo()
                        Log.d("getStudentInfo", "locationInfo: $locationInfo")
                        //authInfo.accessToken
                        val privateInfoF = PrivateInfoExtended(
                            fbId = fbInfo.fbId,
                            fbLink = fbInfo.fbLink,
                            pic = fbInfo.pic,
                            bloodGroup = privateInfo.bloodGroup,
                            email = privateInfo.email,
                            permanentHouse = privateInfo.permanentHouse,
                            ip = locationInfo.ip!!,
                            loc = locationInfo.loc!!
                        ) //paymentInfo //registeredCourse //liveResultInfoList
                        myRef.update("token" , authInfo.accessToken)
                        myRef.update("privateInfo" , privateInfoF)
                        myRef.update("registeredCourse" , registeredCourse)
                        myRef.update("liveResultInfoList" , liveResultInfoList)

                        Log.d("TAG", "getStudentInfoFinal: $privateInfo")
                        send(Resource.Success(UserProfile(token=authInfo.accessToken)))

                    } catch (e: HttpException) {
                        send(Resource.Error("Oops, something went wrong."))
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
    private suspend fun getCgpa(
        id: String,
        semesterList: List<String> = getSemesterList(id),
        loading: (index: Int) -> Unit,
        success: (cgpa: Double, fullResultInfo: List<FullResultInfo>) -> Unit
    ) {
        var weightedCgpa = 0.0
        var totalCreditWeight = 0.0
        var resultFound = 0
        val fullResultInfo = mutableListOf<FullResultInfo>()
        semesterList.forEachIndexed { i, semesterId ->
            GlobalScope.launch(Dispatchers.IO) {
                val semesterResultInfo = FullResultInfo()
                try {
                    delay(i * 100.toLong())
                    val resultInfo = api.getResultInfo(semesterId, id)
                    Log.d("TAG", "getCgpa $semesterId resultInfo: $resultInfo")
                    if (resultInfo.isNotEmpty()) {

                        loading.invoke(semesterList.size - i)
                        var creditTaken = 0.0
                        val rInfo = mutableListOf<ResultInfo>()
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
                        if (!cgpa.isNaN())
                            cgpa = (cgpa * 100.0).roundToInt() / 100.0
                        success.invoke(cgpa, fullResultInfo)
                    }
                } catch (e: HttpException) {
                    loading.invoke(-1)
                    Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                    this.cancel()

                } catch (e: EOFException) {
                    success.invoke(0.0, emptyList())
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

    private fun getSemesterList(id: String): List<String> {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val endYearSemesterCount =
            if (month < 4) 1
            else if (month < 8) 2
            else 3

        val yearEnd = year % 100
        val initial = id.slice(0..2).toInt()  //.split('-')[0].toInt()
        val yr: Int = initial / 10
        var semester: Int = initial % 10
        val list = mutableListOf<String>()

        for (y in yr..yearEnd) {
            for (s in semester..3) {
                list.add(y.toString() + s.toString())
                if (y == yearEnd && s == endYearSemesterCount)
                    break
            }
            semester = 1
        }
        return list
    }


    fun getData(data: FBResponse, success: (data: FBResponse) -> Unit) {
        val graphRequests = GraphRequestBatch(
            GraphRequest.newGraphPathRequest(
                data.accessToken,
                "/me?fields=id,link"
            ) {
                Log.d("req1", "facebook:onSuccess:${it.jsonObject}")
                data.fbId = it.jsonObject?.getString("id").toString()
                data.fbLink = it.jsonObject?.getString("link").toString()
            },
            GraphRequest.newGraphPathRequest(
                data.accessToken,
                "/me/picture?width=480&height=480&redirect=false"
            ) {
                Log.d("req2", "facebook:onSuccess:${it.jsonObject}")
                data.pic = it.jsonObject?.getJSONObject("data")?.getString("url").toString()
            })
        graphRequests.addCallback {
            Log.d("addCallback", "getData: hii")
            success.invoke(data)
        }
        graphRequests.executeAsync()
    }
}
