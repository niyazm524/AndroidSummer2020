package ru.itis.androidsummer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Server (val name: String, val id: Long,val port: Int) : Parcelable {
    constructor() : this("",0,0)
}
