package com.example.muv.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val id: String,
    val name: String,
    val key: String,
    val site: String,
    val type: String,
    val size: Int,
    val official: Boolean,
    val publishedAt: String
) : Parcelable {

    val isYoutube: Boolean
        get() = site.equals("YouTube", ignoreCase = true)

    val isTrailer: Boolean
        get() = type.equals("Trailer", ignoreCase = true)

    val thumbnailUrl: String
        get() = if (isYoutube) {
            "https://img.youtube.com/vi/$key/hqdefault.jpg"
        } else {
            ""
        }

    val youtubeUrl: String
        get() = if (isYoutube) {
            "https://www.youtube.com/watch?v=$key"
        } else {
            ""
        }
}