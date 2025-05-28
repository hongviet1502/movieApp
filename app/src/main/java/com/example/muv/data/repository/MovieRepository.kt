package com.example.muv.data.repository

import com.example.muv.data.model.Movie
import com.example.muv.data.model.MovieDetail
import com.example.muv.data.model.Resource
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getPopularMovies(page: Int): Flow<Resource<List<Movie>>>
    suspend fun getTopRatedMovies(page: Int): Flow<Resource<List<Movie>>>
    suspend fun getNowPlayingMovies(page: Int): Flow<Resource<List<Movie>>>
    suspend fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>>
    suspend fun searchMovies(query: String, page: Int): Flow<Resource<List<Movie>>>
    fun getFavoriteMovies(): Flow<List<Movie>>
    suspend fun addToFavorites(movie: Movie)
    suspend fun removeFromFavorites(movieId: Int)
    suspend fun isFavorite(movieId: Int): Boolean
}