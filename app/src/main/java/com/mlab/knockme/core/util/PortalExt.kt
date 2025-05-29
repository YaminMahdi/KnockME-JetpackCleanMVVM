package com.mlab.knockme.core.util


private val STUDENT_ID_PATTERN = """\d{3}-\d{2}-\d{3,}""".toRegex()

fun String.toStudentRealIdFromEmail(): String? {
    return STUDENT_ID_PATTERN.find(this)?.value
}

fun String.toStudentRealId(programId: String, serialLength: Int = 3): String? {
    if (matches(STUDENT_ID_PATTERN)) return this
    if (length < 6 + serialLength || serialLength < 1) return null
    val semesterId = substring(2, 5)
    val studentSerial = takeLast(serialLength)
    return "$semesterId-$programId-$studentSerial"
}