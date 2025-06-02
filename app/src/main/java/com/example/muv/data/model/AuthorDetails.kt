package com.example.muv.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuthorDetails(
    val name: String?,
    val username: String,
    val avatarPath: String?,
    val rating: Double?
) : Parcelable {

    val avatarUrl: String
        get() = avatarPath?.let { path ->
            if (path.startsWith("/https")) {
                path.substring(1) // Remove leading slash for external URLs
            } else {
                "https://image.tmdb.org/t/p/w185$path"
            }
        } ?: ""

    val displayName: String
        get() = name?.takeIf { it.isNotBlank() } ?: username
}