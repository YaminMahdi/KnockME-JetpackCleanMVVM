package com.mlab.knockme.core.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mlab.knockme.auth_feature.domain.model.ProgramInfo
import com.mlab.knockme.auth_feature.domain.model.ProgramList
import com.mlab.knockme.auth_feature.domain.model.PublicInfo
import com.mlab.knockme.pref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


private val STUDENT_ID_PATTERN = """\d{3}-\d{2}-\d{3,}""".toRegex()

fun String.toStudentRealIdFromEmail(): String? {
    return STUDENT_ID_PATTERN.find(this)?.value
}

fun String.toStudentRealId(programId: String?, serialLength: Int = 3): String? {
    if (matches(STUDENT_ID_PATTERN)) return this
    if (length < 6 + serialLength || serialLength < 1 || programId.isNullOrEmpty()) return null
    val semesterId = substring(3, 6)
    val studentSerial = takeLast(serialLength)
    return "$semesterId-$programId-$studentSerial"
}

suspend fun FirebaseFirestore.addMyProgram(publicInfo: PublicInfo){
    withContext(Dispatchers.IO){
        try {
            val newProgram = publicInfo.toProgramInfo()
            if(newProgram.shortName.isEmpty() || newProgram.programId.isEmpty()) return@withContext
            val programRef = collection("program").document("list")
            val localList = pref.readObject<List<ProgramInfo>>(PrefKeys.PROGRAM_LIST).orEmpty()
            if (!localList.any { it.programId == newProgram.programId }){
                val serverList = programRef.get().await().toObject<ProgramList>()?.list.orEmpty().toMutableList()
                if (!serverList.any { it.programId == newProgram.programId }){
                    serverList.add(newProgram)
                    serverList.sortBy { it.shortName }
                    programRef.set(ProgramList(serverList)).await()
                    pref.saveObject(PrefKeys.PROGRAM_LIST, serverList)
                }
            }

        } catch (e: Exception) {
            Log.d("TAG", "addNewProgram: ${e.message}")
        }
    }
}