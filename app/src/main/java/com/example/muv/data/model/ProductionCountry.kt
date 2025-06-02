package com.example.muv.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductionCountry(
    val iso31661: String,
    val name: String
) : Parcelable