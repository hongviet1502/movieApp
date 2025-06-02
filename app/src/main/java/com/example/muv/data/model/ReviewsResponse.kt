package com.example.muv.data.model

data class ReviewsResponse(
    val id: Int,
    val page: Int,
    val results: List<Review>,
    val totalPages: Int,
    val totalResults: Int
)