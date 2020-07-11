package ru.itis.androidsummer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Player (val name: String,val ip: String, val port: Int, val rating: Int, val id: Long) : Parcelable {
    constructor() : this("","",0,0,0)
}
