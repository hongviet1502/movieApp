package com.example.muv.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Review(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val url: String,
    val rating: Char?,
    val authorDetails: AuthorDetails?
) : Parcelable {

    val shortContent: String
        get() = if (content.length > 200) {
            content.substring(0, 200) + "..."
        } else {
            content
        }
}