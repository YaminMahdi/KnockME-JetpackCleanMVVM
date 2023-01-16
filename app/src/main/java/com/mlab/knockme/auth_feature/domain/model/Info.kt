package com.mlab.knockme.auth_feature.domain.model

data class Info(
    val publicInfo: PublicInfo=PublicInfo(),
    val privateInfo: PrivateInfo=PrivateInfo(),
    val paymentInfo: PaymentInfo=PaymentInfo(),
)
