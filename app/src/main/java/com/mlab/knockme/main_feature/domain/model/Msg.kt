package com.mlab.knockme.main_feature.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Msg(
    val id: String?="",
    val nm: String?="",
    val msg: String?="",
    val pic: String?="",
    val time: Long?=0L
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nm)
        parcel.writeString(msg)
        parcel.writeString(pic)
        parcel.writeValue(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Msg> {
        override fun createFromParcel(parcel: Parcel): Msg {
            return Msg(parcel)
        }

        override fun newArray(size: Int): Array<Msg?> {
            return arrayOfNulls(size)
        }
    }
}

class InvalidMsgExp(msg: String):Exception(msg)
