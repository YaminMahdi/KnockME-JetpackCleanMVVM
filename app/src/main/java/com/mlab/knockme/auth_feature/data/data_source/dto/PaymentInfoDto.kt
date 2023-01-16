package com.mlab.knockme.auth_feature.data.data_source.dto

import com.mlab.knockme.auth_feature.domain.model.PaymentInfo

data class PaymentInfoDto(
    val totalCredit: Double,
    val totalDebit: Double,
    val totalOther: Double
) {
    fun toPaymentInfo() =
        PaymentInfo(totalCredit, totalDebit, totalOther)
}