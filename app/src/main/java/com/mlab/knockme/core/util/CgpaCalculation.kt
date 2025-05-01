package com.mlab.knockme.core.util

import android.util.Log
import com.mlab.knockme.auth_feature.data.data_source.PortalApi
import com.mlab.knockme.auth_feature.domain.model.FullResultInfo
import com.mlab.knockme.auth_feature.domain.model.ResultInfo
import kotlinx.coroutines.*
import java.util.Calendar
import kotlin.math.roundToInt

@OptIn(DelicateCoroutinesApi::class)
fun getCgpa(
    id: String,
    api: PortalApi,
    semesterList: List<String>,
    loading: (index: Int) -> Unit,
    success: (cgpa: Double, totalCompletedCredit: Double, fullResultInfo: List<FullResultInfo>) -> Unit,
    useSuccessOnly: Boolean = false
) {
    if(semesterList.isEmpty()){
        success.invoke(0.0,0.0, emptyList())
    }
    var totalCompletedCredit =0.0
    var weightedCgpa = 0.0
    var nullSemester = 0
    var apiCallCount = 0
    //var totalCreditWeight = 0.0
    val courseScoreMap : HashMap<String, Double> = HashMap<String, Double> ()
    //val totalWeightMap : HashMap<String, Double> = HashMap<String, Double> ()
    //var resultFound = 0
    val fullResultInfo = mutableListOf<FullResultInfo>()
    semesterList.forEachIndexed { i, semesterId ->
        GlobalScope.launch(Dispatchers.IO) {
            val semesterResultInfo = FullResultInfo()
            try {
                if(i!=0)
                    delay(i * 250L + (0..100).random())
                val resultInfo = tryGet { api.getResultInfo(semesterId, id).body() }
                Log.d("TAG", "getCgpa $semesterId resultInfo: $resultInfo")
                if (!resultInfo.isNullOrEmpty()) {
                    loading.invoke(i+1-nullSemester)
                    var creditTaken = 0.0
                    val rInfo = arrayListOf<ResultInfo>()
                    resultInfo.forEach { courseInfo ->
                        if(courseInfo.pointEquivalent!=0.0){
                            if(!courseScoreMap.containsKey(courseInfo.customCourseId)) {
                                courseScoreMap[courseInfo.customCourseId] = courseInfo.pointEquivalent * courseInfo.totalCredit
                                totalCompletedCredit += courseInfo.totalCredit
                            }else{
                                val oldCgpaScore = courseScoreMap[courseInfo.customCourseId]!!
                                var newCgpaScore = courseInfo.pointEquivalent * courseInfo.totalCredit
                                if(oldCgpaScore > newCgpaScore) newCgpaScore = oldCgpaScore
                                courseScoreMap[courseInfo.customCourseId] = newCgpaScore
                            }
                        }
                        //weightedCgpa += courseInfo.pointEquivalent * courseInfo.totalCredit
                        creditTaken += courseInfo.totalCredit
                        courseInfo.customCourseId
                        rInfo.add(courseInfo.toResultInfo())
                    }
                    //totalCreditWeight += creditTaken

                    //add a semester result to list
                    semesterResultInfo.resultInfo = rInfo
                    run lit@{
                        resultInfo.forEach{
                            semesterResultInfo.semesterInfo = it.toSemesterInfo(creditTaken)
                            if(it.cgpa!=0.0) return@lit
                        }
                    }
                    fullResultInfo.add(semesterResultInfo)  //adding
                }
                else{
                    nullSemester++
                }
                apiCallCount++
                if(apiCallCount == semesterList.size){
                    weightedCgpa += courseScoreMap.values.sum()
                    var cgpa = weightedCgpa / totalCompletedCredit
                    cgpa = if(!cgpa.isNaN()) (cgpa* 100.0).roundToInt() / 100.0 else 0.0
//                    delay(300)
                    fullResultInfo.sortBy { it.semesterInfo.semesterId }
                    success.invoke(cgpa, totalCompletedCredit, fullResultInfo)
                }
//                resultFound++
//                if (resultFound == semesterList.size) {
//                    var cgpa = weightedCgpa / totalCompletedCredit
//                    cgpa = if(!cgpa.isNaN()) (cgpa* 100.0).roundToInt() / 100.0 else 0.0
//
//                    fullResultInfo.sortBy { it.semesterInfo.semesterId }
//                    success.invoke(cgpa, fullResultInfo)
//                }
            }
//            catch (e: EOFException) {
//                success.invoke(0.0,0.0, arrayListOf())
//                Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
//                this.cancel()
//            } catch (e: IOException) {
//                if(!useSuccessOnly)
//                    loading.invoke(-2)
//                Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
//                this.cancel()
//            }
            catch (e: Exception) {
                if(!useSuccessOnly)
                    loading.invoke(-1)
                Log.d("TAG", "getCgpa: ${e.message} ${e.localizedMessage} $semesterId")
                this.cancel()
            }
            //this.cancel()
        }
    }
}
fun getSemesterList(firstSemId: Int): List<String> {
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