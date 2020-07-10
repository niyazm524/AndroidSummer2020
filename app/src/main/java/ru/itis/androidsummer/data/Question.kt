package ru.itis.androidsummer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question (val price: Int, val question: String, val answer: String) : Parcelable {
    constructor() : this(0,"","")
}
