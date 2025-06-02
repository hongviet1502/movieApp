package com.example.muv.data.repository

import com.example.muv.data.model.CastMember
import com.example.muv.data.model.Genre
import com.example.muv.data.model.Movie
import com.example.muv.data.model.MovieDetail
import com.example.muv.data.model.Resource
import com.example.muv.data.model.Review
import com.example.muv.data.model.Video
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun getPopularMovies(page: Int = 1): Flow<Resource<List<Movie>>>
    suspend fun getTopRatedMovies(page: Int = 1): Flow<Resource<List<Movie>>>
    suspend fun getNowPlayingMovies(page: Int = 1): Flow<Resource<List<Movie>>>
    suspend fun searchMovies(query: String, page: Int = 1): Flow<Resource<List<Movie>>>

    suspend fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetail>>
    suspend fun getMovieCast(movieId: Int): Flow<Resource<List<CastMember>>>
    suspend fun getMovieVideos(movieId: Int): Flow<Resource<List<Video>>>
    suspend fun getSimilarMovies(movieId: Int, page: Int = 1): Flow<Resource<List<Movie>>>
    suspend fun getMovieReviews(movieId: Int, page: Int = 1): Flow<Resource<List<Review>>>
    suspend fun getRecommendedMovies(movieId: Int, page: Int = 1): Flow<Resource<List<Movie>>>

    // Favorite management
    suspend fun addToFavorites(movie: Movie)
    suspend fun removeFromFavorites(movieId: Int)
    suspend fun getFavoriteMovies(): Flow<Resource<List<Movie>>>
    suspend fun isMovieFavorite(movieId: Int): Boolean

    // Additional utility methods
    suspend fun getTrendingMovies(): Flow<Resource<List<Movie>>>
    suspend fun getMovieGenres(): Flow<Resource<List<Genre>>>
}