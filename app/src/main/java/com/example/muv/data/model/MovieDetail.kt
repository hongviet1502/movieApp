package com.example.muv.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDetail(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtime: Int?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val adult: Boolean,
    val genres: List<Genre>,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<ProductionCountry>,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val tagline: String?,
    val budget: Long,
    val revenue: Long,
    val homepage: String?
) : Parcelable {

    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w500${posterPath}"

    val backdropUrl: String
        get() = "https://image.tmdb.org/t/p/w1280${backdropPath}"

    val releaseYear: String
        get() = releaseDate?.substring(0, 4) ?: "Unknown"

    val formattedRuntime: String
        get() = runtime?.let { "${it} min" } ?: "Unknown"

    val genreNames: String
        get() = genres.joinToString(", ") { it.name }
}