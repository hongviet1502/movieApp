package com.example.muv.data.repository

import com.example.muv.BuildConfig
import com.example.muv.data.api.ApiService
import com.example.muv.data.local.dao.MovieDao
import com.example.muv.data.local.entity.toFavoriteMovie
import com.example.muv.data.local.entity.toMovie
import com.example.muv.data.model.Movie
import com.example.muv.data.model.MovieDetail
import com.example.muv.data.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) : MovieRepository {

    override suspend fun getPopularMovies(page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getPopularMovies(BuildConfig.API_KEY, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    emit(Resource.Success(movieResponse.results))
                } ?: emit(Resource.Error("Không có dữ liệu"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getTopRatedMovies(page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getTopRatedMovies(BuildConfig.API_KEY, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    emit(Resource.Success(movieResponse.results))
                } ?: emit(Resource.Error("Không có dữ liệu"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getNowPlayingMovies(page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getNowPlayingMovies(BuildConfig.API_KEY, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    emit(Resource.Success(movieResponse.results))
                } ?: emit(Resource.Error("Không có dữ liệu"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMovieDetail(movieId, BuildConfig.API_KEY)
            if (response.isSuccessful) {
                response.body()?.let { movieDetail ->
                    emit(Resource.Success(movieDetail))
                } ?: emit(Resource.Error("Không có dữ liệu"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun searchMovies(query: String, page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.searchMovies(BuildConfig.API_KEY, query, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    emit(Resource.Success(movieResponse.results))
                } ?: emit(Resource.Error("Không có dữ liệu"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return movieDao.getFavoriteMovies().map { favoriteMovies ->
            favoriteMovies.map { it.toMovie() }
        }
    }

    override suspend fun addToFavorites(movie: Movie) {
        movieDao.insertFavorite(movie.toFavoriteMovie())
    }

    override suspend fun removeFromFavorites(movieId: Int) {
        movieDao.deleteFavoriteById(movieId)
    }

    override suspend fun isFavorite(movieId: Int): Boolean {
        return movieDao.isFavorite(movieId)
    }
}