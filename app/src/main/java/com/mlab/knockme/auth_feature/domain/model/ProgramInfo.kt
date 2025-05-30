package com.mlab.knockme.auth_feature.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProgramInfo(
    val programId: String = "", // 17
    val id: Int = 0, // 8
    val name: String = "", // Master of Science in Management Information System
    val shortName: String = "", // M.S. in MIS
    val typeId: Int = 0, // 2
    val typeName: String = "" // MASTERS
): Parcelable

data class ProgramList(
    val list: List<ProgramInfo> = listOf()
)
//{
//    "studentId": "242-11-188", 0242420004081188
//    "fkCampus": "C03",
//    "campusName": "DSC",
//    "studentName": "Md. Jahinul Islam Bhuiyan Rabbu",
//    "batchId": "11-67",
//    "batchNo": 67,
//    "programCredit": 0,
//    "programId": "11",
//    "programName": "Bachelor of Business Administration",
//    "progShortName": "B.B.A.",
//    "programType": "BACHELOR",
//    "deptShortName": "Business Admin",
//    "departmentName": "Business Administration",
//    "facultyName": "Faculty of Business & Entrepreneurship",
//    "facShortName": "FBE",
//    "semesterId": "243",
//    "semesterName": "Fall 2024",
//    "shift": "MORNING"
//}