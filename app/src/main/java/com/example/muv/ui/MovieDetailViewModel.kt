package com.example.muv.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.muv.data.model.*
import com.example.muv.data.repository.MovieRepository
import com.example.muv.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : BaseViewModel() {

    private var currentMovieId: Int = 0

    // Movie details
    private val _movieDetails = MutableLiveData<MovieDetail?>()
    val movieDetails: LiveData<MovieDetail?> = _movieDetails

    // Cast information
    private val _cast = MutableLiveData<List<CastMember>>()
    val cast: LiveData<List<CastMember>> = _cast

    // Videos/Trailers
    private val _videos = MutableLiveData<List<Video>>()
    val videos: LiveData<List<Video>> = _videos

    // Similar movies
    private val _similarMovies = MutableLiveData<List<Movie>>()
    val similarMovies: LiveData<List<Movie>> = _similarMovies

    // Reviews
    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    // Favorite status
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    // Custom loading state for movie detail (in addition to base loading)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadMovieDetails(movieId: Int) {
        currentMovieId = movieId
        resetStates()

        viewModelScope.launch {
            try {
                _isLoading.value = true
                showLoading()
                // Load all movie data concurrently
                launch { loadMovieDetailsData(movieId) }
                launch { loadMovieCast(movieId) }
                launch { loadMovieVideos(movieId) }
                launch { loadSimilarMovies(movieId) }
                launch { loadMovieReviews(movieId) }
                launch { checkFavoriteStatus(movieId) }

            } catch (e: Exception) {
                showError("Failed to load movie details: ${e.message}")
                _isLoading.value = false
                hideLoading()
            }
        }
    }

    private suspend fun loadMovieDetailsData(movieId: Int) {
        try {
            movieRepository.getMovieDetails(movieId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Loading handled at parent level
                    }
                    is Resource.Success -> {
                        _movieDetails.value = resource.data
                        _isLoading.value = false
                        hideLoading()

                    }
                    is Resource.Error -> {
                        showError(resource.message ?: "Failed to load movie details")
                        _isLoading.value = false
                        hideLoading()
                    }
                }
            }
        } catch (e: Exception) {
            showError("Error loading movie details: ${e.message}")
            _isLoading.value = false
            hideLoading()
        }
    }

    private suspend fun loadMovieCast(movieId: Int) {
        try {
            movieRepository.getMovieCast(movieId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _cast.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        // Don't show error for cast, just log it
                        _cast.value = emptyList()
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            _cast.value = emptyList()
        }
    }

    private suspend fun loadMovieVideos(movieId: Int) {
        try {
            movieRepository.getMovieVideos(movieId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _videos.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _videos.value = emptyList()
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            _videos.value = emptyList()
        }
    }

    private suspend fun loadSimilarMovies(movieId: Int) {
        try {
            movieRepository.getSimilarMovies(movieId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _similarMovies.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _similarMovies.value = emptyList()
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            _similarMovies.value = emptyList()
        }
    }

    private suspend fun loadMovieReviews(movieId: Int) {
        try {
            movieRepository.getMovieReviews(movieId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _reviews.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _reviews.value = emptyList()
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            _reviews.value = emptyList()
        }
    }

    private suspend fun checkFavoriteStatus(movieId: Int) {
        try {
            val isFav = movieRepository.isMovieFavorite(movieId)
            _isFavorite.value = isFav
        } catch (e: Exception) {
            _isFavorite.value = false
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                val movie = _movieDetails.value ?: return@launch
                val currentStatus = _isFavorite.value ?: false

                if (currentStatus) {
                    movieRepository.removeFromFavorites(movie.id)
                    _isFavorite.value = false
                    showMessage("Removed from My List")
                } else {
                    val movieToAdd = Movie(
                        id = movie.id,
                        title = movie.title,
                        overview = movie.overview,
                        posterPath = movie.posterPath,
                        backdropPath = movie.backdropPath,
                        releaseDate = movie.releaseDate.toString(),
                        voteAverage = movie.voteAverage,
                        voteCount = movie.voteCount,
                        genreIds = movie.genres.map { it.id }
                    )
                    movieRepository.addToFavorites(movieToAdd)
                    _isFavorite.value = true
                    showMessage("Added to My List")
                }
            } catch (e: Exception) {
                showError("Failed to update favorites: ${e.message}")
            }
        }
    }

    fun retryLoadMovieDetails() {
        if (currentMovieId != 0) {
            loadMovieDetails(currentMovieId)
        }
    }

    fun refreshMovieDetails() {
        loadMovieDetails(currentMovieId)
    }

    private fun resetStates() {
        _movieDetails.value = null
        _cast.value = emptyList()
        _videos.value = emptyList()
        _similarMovies.value = emptyList()
        _reviews.value = emptyList()
        _isFavorite.value = false
//        clearError()
    }
}