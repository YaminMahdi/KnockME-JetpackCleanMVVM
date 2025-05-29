package com.mlab.knockme.auth_feature.domain.model

data class PrivateInfo(
    val firstName: String? = "",
    val lastName: String? = "",
    val bloodGroup: String? = "",
    val email: String? = "",
    val presentHouse: String? = "",
    val permanentHouse: String? = "",
    val sex: String? = "",
    val mobile: String? = "",
    val notes: String? = "",
    val socialNetId: String? = "",
    val birthDate: String? = "",
    val placeOfBirth: String? = "",
    val religion: String? = "",
    val fatherMobile: String? = "",
    val motherMobile: String? = "",
)

fun PrivateInfo?.toPrivateInfoExtended(
    fbId: String? = "",
    fbLink: String? = "",
    pic: String? = "",
    ip: String? = "",
    loc: String? = "",
) = PrivateInfoExtended(
    name = (this?.firstName.orEmpty() + " " + this?.lastName.orEmpty()).trim(),
    bloodGroup = this?.bloodGroup,
    email = this?.email,
    presentHouse = this?.presentHouse,
    permanentHouse = this?.permanentHouse,
    sex = this?.sex,
    mobile = this?.mobile,
    notes = this?.notes,
    socialNetId = this?.socialNetId,
    birthDate = this?.birthDate,
    placeOfBirth = this?.placeOfBirth,
    religion = this?.religion,
    fatherMobile = this?.fatherMobile,
    motherMobile = this?.motherMobile,
    fbId = fbId,
    fbLink = fbLink,
    pic = pic,
    ip = ip,
    loc = loc
)
