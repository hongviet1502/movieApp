package com.example.muv.data.model

import com.google.gson.annotations.SerializedName

data class PersonDetail(
    val id: Int,
    val name: String,
    val biography: String?,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("place_of_birth")
    val placeOfBirth: String?,
    val birthday: String?,
    val deathday: String?,
    @SerializedName("known_for_department")
    val knownForDepartment: String,
    val popularity: Double,
    @SerializedName("also_known_as")
    val alsoKnownAs: List<String>,
    val gender: Int,
    val adult: Boolean,
    val imdbId: String?,
    val homepage: String?
) {
    val profileUrl: String
        get() = "https://image.tmdb.org/t/p/w500${profilePath}"

    val age: Int?
        get() = birthday?.let { birth ->
            try {
                val birthYear = birth.substring(0, 4).toInt()
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                currentYear - birthYear
            } catch (e: Exception) {
                null
            }
        }
}