package com.example.muv.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?,
    val order: Int,
    val castId: Int,
    val creditId: String,
    val gender: Int?,
    val knownForDepartment: String,
    val popularity: Double
) : Parcelable {

    val profileUrl: String
        get() = "https://image.tmdb.org/t/p/w185${profilePath}"
}
