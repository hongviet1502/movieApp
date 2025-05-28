package com.example.muv.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.muv.data.model.Movie

@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val addedAt: Long = System.currentTimeMillis()
)

// Extension function to convert between entities and domain models
fun FavoriteMovie.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genreIds = emptyList()
    )
}

fun Movie.toFavoriteMovie(): FavoriteMovie {
    return FavoriteMovie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}