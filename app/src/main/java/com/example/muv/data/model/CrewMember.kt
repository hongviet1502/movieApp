package com.example.muv.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CrewMember(
    val id: Int,
    val name: String,
    val job: String,
    val department: String,
    val profilePath: String?,
    val creditId: String,
    val gender: Int?,
    val knownForDepartment: String,
    val popularity: Double
) : Parcelable