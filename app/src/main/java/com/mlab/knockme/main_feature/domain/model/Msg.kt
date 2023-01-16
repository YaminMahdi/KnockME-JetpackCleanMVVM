package com.mlab.knockme.main_feature.domain.model

data class Msg(
    val id: String="",
    val nm: String="",
    val msg: String="",
    val pic: String="",
    val time: Long=0
)

class InvalidMsgExp(msg: String):Exception(msg)
