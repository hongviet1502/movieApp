package com.example.muv.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpokenLanguage(
    val iso6391: String,
    val name: String,
    val englishName: String
) : Parcelable