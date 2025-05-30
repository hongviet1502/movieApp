package com.example.muv.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.muv.data.model.Movie
import com.example.muv.data.model.Resource
import com.example.muv.data.repository.MovieRepository
import com.example.muv.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : BaseViewModel() {

    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    private val _topRatedMovies = MutableLiveData<List<Movie>>()
    val topRatedMovies: LiveData<List<Movie>> = _topRatedMovies

    private val _nowPlayingMovies = MutableLiveData<List<Movie>>()
    val nowPlayingMovies: LiveData<List<Movie>> = _nowPlayingMovies

    init {
        loadAllMovies()
    }

    fun loadAllMovies() {
        loadPopularMovies()
        loadTopRatedMovies()
        loadNowPlayingMovies()
    }

    private fun loadPopularMovies() {
        viewModelScope.launch {
            movieRepository.getPopularMovies(1).collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideLoading()
                        _popularMovies.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        showError(resource.message ?: "Lỗi không xác định")
                    }
                }
            }
        }
    }

    private fun loadTopRatedMovies() {
        viewModelScope.launch {
            movieRepository.getTopRatedMovies(1).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _topRatedMovies.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        showError(resource.message ?: "Lỗi tải phim đánh giá cao")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadNowPlayingMovies() {
        viewModelScope.launch {
            movieRepository.getNowPlayingMovies(1).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _nowPlayingMovies.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        showError(resource.message ?: "Lỗi tải phim đang chiếu")
                    }
                    else -> {}
                }
            }
        }
    }
}