package com.mlab.knockme.auth_feature.data.data_source

import com.mlab.knockme.auth_feature.data.data_source.dto.*
import com.mlab.knockme.core.util.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


data class LoginInformation (
    var username: String,
    var password: String
)

interface PortalApi {

    //public id info
    @GET("/result/studentInfo")
    suspend fun getStudentIdInfo(
        @Query("studentId") id : String
    ) : Response<StudentInfoDto>

    //authentication
//    @Headers("Accept: */*", "Content-Type: application/json")
    @POST("/login")
    suspend fun getAuthInfo(
        @Body loginInformation : LoginInformation
    ) : Response<LoginInfoDto>

    //private id info
    @GET("/profile/studentInfo")
    suspend fun getPrivateInfo(
        @Header("accessToken") accessToken : String
    ) : Response<PrivateInfoDto>

    //result by semester
    @GET("/result")
    suspend fun getResultInfo(
        @Query("semesterId") semesterId : String,
        @Query("studentId") studentId : String
    ) : Response<List<ResultInfoDto>>


    //completed semester list
    @GET("/dashboard/studentSGPAGraph")
    suspend fun getSGPAGraph(
        @Header("accessToken") accessToken : String
    ) : List<SGPAGraphDto>?  //[0].semesterId

     @GET("/registeredCourse/semesterList")
    suspend fun getAllSemesterInfo(
        @Header("accessToken") accessToken : String
    ) : List<SemesterInfoDto>?  //[0].semesterId


    //registered course by semesterId
    @GET("/registeredCourse")
    suspend fun getRegisteredCourse(
        @Query("semesterId") semesterId : String,
        @Header("accessToken") accessToken : String
    ) : Response<List<CourseInfoDto>>  //courseSectionId

    //live result by courseSectionId
    @GET("/liveResult")
    suspend fun getLiveResult(
        @Query("courseSectionId") courseSectionId : Int,
        @Header("accessToken") accessToken : String
    ) : Response<LiveResultInfoDto>


    //payment info
    @GET("/paymentLedger/paymentLedgerSummery")
    suspend fun getPaymentInfo(
        @Header("accessToken") accessToken : String
    ) : PaymentInfoDto?

    //payment info
    @GET("/accounts/semester-exam-clearance")
    suspend fun getClearanceInfo(
        @Header("accessToken") accessToken : String
    ) : List<ClearanceInfoDto>?

    //ip info without base url
    @GET(Constants.IP_URL)
    suspend fun getLocationInfo(
        @Query("fields") fields : String="status,country,countryCode,regionName,city,district,query"
    ): LocationInfoDto?

//    @GET
//    fun getRedirectUrl(@Url url: String): Call<String>

    @GET
    suspend fun getImgByteStream(@Url url: String): ResponseBody

    @GET(Constants.HADITH_URL)
    suspend fun getDailyHadith(): List<DailyHadithDto>?

    @GET(Constants.ADMISSION_URL)
    suspend fun getAdmissionInfo(): Response<AdmissionDto>

//    @Query("fields") fields : List<String> =
//    listOf(
//    "status","country","countryCode",
//    "regionName","city","district","query"
//    )
}