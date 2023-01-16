package com.mlab.knockme.auth_feature.domain.model

data class PaymentInfo(
    val totalCredit: Double?=0.0,
    val totalDebit: Double?=0.0,
    val totalOther: Double?=0.0
)
