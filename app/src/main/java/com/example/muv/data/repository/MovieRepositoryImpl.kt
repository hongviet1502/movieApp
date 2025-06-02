package com.example.muv.data.repository

import com.example.muv.BuildConfig
import com.example.muv.data.api.ApiService
import com.example.muv.data.local.dao.FavoriteMovieDao
import com.example.muv.data.local.entity.FavoriteMovieEntity
import com.example.muv.data.model.CastMember
import com.example.muv.data.model.Genre
import com.example.muv.data.model.Movie
import com.example.muv.data.model.MovieDetail
import com.example.muv.data.model.Resource
import com.example.muv.data.model.Review
import com.example.muv.data.model.Video
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val favoriteMovieDao: FavoriteMovieDao
) : MovieRepository {

    // Existing methods
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

    override suspend fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetail>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMovieDetails(movieId, BuildConfig.API_KEY)
            if (response.isSuccessful) {
                response.body()?.let { movieDetail ->
                    emit(Resource.Success(movieDetail))
                } ?: emit(Resource.Error("Không tìm thấy chi tiết phim"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getMovieCast(movieId: Int): Flow<Resource<List<CastMember>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMovieCredits(movieId, BuildConfig.API_KEY)
            if (response.isSuccessful) {
                response.body()?.let { creditsResponse ->
                    // Sort cast by order and take first 10
                    val sortedCast = creditsResponse.cast
                        .sortedBy { it.order }
                        .take(10)
                    emit(Resource.Success(sortedCast))
                } ?: emit(Resource.Error("Không có thông tin diễn viên"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getMovieVideos(movieId: Int): Flow<Resource<List<Video>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMovieVideos(movieId, BuildConfig.API_KEY)
            if (response.isSuccessful) {
                response.body()?.let { videosResponse ->
                    // Filter for trailers and teasers, prioritize trailers
                    val filteredVideos = videosResponse.results
                        .filter { it.site.equals("YouTube", ignoreCase = true) }
                        .filter {
                            it.type.equals("Trailer", ignoreCase = true) ||
                                    it.type.equals("Teaser", ignoreCase = true) ||
                                    it.type.equals("Clip", ignoreCase = true)
                        }
                        .sortedWith(compareBy<Video> {
                            when (it.type.lowercase()) {
                                "trailer" -> 0
                                "teaser" -> 1
                                "clip" -> 2
                                else -> 3
                            }
                        }.thenByDescending { it.official })
                        .take(5)

                    emit(Resource.Success(filteredVideos))
                } ?: emit(Resource.Error("Không có video"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getSimilarMovies(movieId: Int, page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getSimilarMovies(movieId, BuildConfig.API_KEY, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    // Take first 10 similar movies
                    val similarMovies = movieResponse.results.take(10)
                    emit(Resource.Success(similarMovies))
                } ?: emit(Resource.Error("Không có phim tương tự"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getMovieReviews(movieId: Int, page: Int): Flow<Resource<List<Review>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMovieReviews(movieId, BuildConfig.API_KEY, page)
            if (response.isSuccessful) {
                response.body()?.let { reviewsResponse ->
                    // Take first 5 reviews and sort by created date
                    val sortedReviews = reviewsResponse.results
                        .sortedByDescending { it.createdAt }
                        .take(5)
                    emit(Resource.Success(sortedReviews))
                } ?: emit(Resource.Error("Không có đánh giá"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getRecommendedMovies(movieId: Int, page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getRecommendedMovies(movieId, BuildConfig.API_KEY, page)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    emit(Resource.Success(movieResponse.results.take(10)))
                } ?: emit(Resource.Error("Không có gợi ý phim"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    // Favorite management methods
    override suspend fun addToFavorites(movie: Movie) {
        try {
            val favoriteEntity = FavoriteMovieEntity(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage,
                voteCount = movie.voteCount,
                genreIds = movie.genreIds.joinToString(","),
                addedAt = System.currentTimeMillis(),
                popularity = movie.popularity
            )
            favoriteMovieDao.insertFavorite(favoriteEntity)
        } catch (e: Exception) {
            throw Exception("Failed to add to favorites: ${e.message}")
        }
    }

    override suspend fun removeFromFavorites(movieId: Int) {
        try {
            favoriteMovieDao.deleteFavoriteById(movieId)
        } catch (e: Exception) {
            throw Exception("Failed to remove from favorites: ${e.message}")
        }
    }

    override suspend fun getFavoriteMovies(): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val favorites = favoriteMovieDao.getAllFavorites()
            val movies = favorites.map { entity ->
                Movie(
                    id = entity.id,
                    title = entity.title,
                    overview = entity.overview,
                    posterPath = entity.posterPath,
                    backdropPath = entity.backdropPath,
                    releaseDate = entity.releaseDate.toString(),
                    voteAverage = entity.voteAverage,
                    voteCount = entity.voteCount,
                    genreIds = entity.genreIds.split(",").mapNotNull { it.toIntOrNull() },
                    isFavorite = true,
                    popularity = entity.popularity
                )
            }
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi khi lấy danh sách yêu thích: ${e.message}"))
        }
    }

    override suspend fun isMovieFavorite(movieId: Int): Boolean {
        return try {
            favoriteMovieDao.isFavorite(movieId)
        } catch (e: Exception) {
            false
        }
    }

    // Additional utility methods
    override suspend fun getTrendingMovies(): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getTrendingMovies(BuildConfig.API_KEY)
            if (response.isSuccessful) {
                response.body()?.let { movieResponse ->
                    emit(Resource.Success(movieResponse.results))
                } ?: emit(Resource.Error("Không có phim trending"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun getMovieGenres(): Flow<Resource<List<Genre>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMovieGenres(BuildConfig.API_KEY)
            if (response.isSuccessful) {
                response.body()?.let { genresResponse ->
                    emit(Resource.Success(genresResponse.genres))
                } ?: emit(Resource.Error("Không có thể loại phim"))
            } else {
                emit(Resource.Error("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }
}