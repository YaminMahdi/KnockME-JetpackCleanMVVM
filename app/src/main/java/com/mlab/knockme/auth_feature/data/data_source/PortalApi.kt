package com.mlab.knockme.auth_feature.data.data_source

import com.mlab.knockme.auth_feature.data.data_source.dto.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*


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
    @Headers(
        "Accept: */*",
        "Content-Type: application/json"
    )
    @POST("/login")
    suspend fun getAuthInfo(
        @Body loginInformation : LoginInformation
    ) : LoginInfoDto?

    //private id info
    @GET("/profile/studentInfo")
    suspend fun getPrivateInfo(
        @Header("accessToken") accessToken : String
    ) : PrivateInfoDto?


    //result by semester
    @GET("/result")
    suspend fun getResultInfo(
        @Query("semesterId") semesterId : String,
        @Query("studentId") studentId : String
    ) : List<ResultInfoDto>?


    //completed semester list
    @GET("/registeredCourse/semesterList")
    suspend fun getAllSemesterInfo(
        @Header("accessToken") accessToken : String
    ) : List<SemesterInfoDto>?  //[0].semesterId


    //registered course by semesterId
    @GET("/registeredCourse")
    suspend fun getRegisteredCourse(
        @Query("semesterId") semesterId : String,
        @Header("accessToken") accessToken : String
    ) : List<CourseInfoDto>?  //courseSectionId

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
    @GET("http://ip-api.com/json")
    suspend fun getLocationInfo(
        //@Url url : String="http://ip-api.com/json/",
        @Query("fields") fields : String="status,country,countryCode,regionName,city,district,query"
    ): LocationInfoDto?

//    @GET
//    fun getRedirectUrl(@Url url: String): Call<String>

    @GET
    suspend fun getImgByteStream(@Url url: String): ResponseBody

    @GET("https://knock-me.github.io/dailyHadith.json")
    suspend fun getDailyHadith(): List<DailyHadithDto>?

//    @Query("fields") fields : List<String> =
//    listOf(
//    "status","country","countryCode",
//    "regionName","city","district","query"
//    )


    companion object{
        const val BASE_URL="http://software.diu.edu.bd:8006/"
    }
}