package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.auth_feature.domain.model.PaymentInfo
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject

class UpdatePaymentInfo @Inject constructor(
    private val repo: MainRepo
) {
    suspend operator fun invoke(
        id: String,
        accessToken: String,
        paymentInfo: PaymentInfo,
        Success: (paymentInfo: PaymentInfo) -> Unit,
        Failed: (msg:String) -> Unit
    )= repo.updatePaymentInfo(id, accessToken, paymentInfo, Success, Failed)
}