package com.example.muv.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDetail(
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
    val runtime: Int?,
    val genres: List<Genre>,
    val tagline: String?
) : Parcelable {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"

    val backdropUrl: String
        get() = "https://image.tmdb.org/t/p/w500$backdropPath"

    val genreNames: String
        get() = genres.joinToString(", ") { it.name }
}