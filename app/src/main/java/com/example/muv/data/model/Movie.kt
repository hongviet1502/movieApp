package com.example.muv.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    var isFavorite: Boolean = false
) : Parcelable {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"

    val backdropUrl: String
        get() = "https://image.tmdb.org/t/p/w500$backdropPath"
}