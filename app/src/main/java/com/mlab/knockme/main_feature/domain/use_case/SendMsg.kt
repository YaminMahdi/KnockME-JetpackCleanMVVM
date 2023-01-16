package com.mlab.knockme.main_feature.domain.use_case

import com.mlab.knockme.main_feature.domain.model.InvalidMsgExp
import com.mlab.knockme.main_feature.domain.model.Msg
import com.mlab.knockme.main_feature.domain.repo.MainRepo
import javax.inject.Inject
import kotlin.jvm.Throws

class SendMsg@Inject constructor(
    private val repo: MainRepo
) {
    @Throws(InvalidMsgExp::class)
    suspend operator fun invoke(path: String, msg: Msg){
        if(msg.msg!!.isBlank())
            throw InvalidMsgExp("Message can't be blank.")
        repo.sendMessages(path,msg)
    }
}