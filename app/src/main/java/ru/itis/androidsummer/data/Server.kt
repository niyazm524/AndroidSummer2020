package ru.itis.androidsummer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Server (val address: String, val name: String, val id: Long) : Parcelable {

}
