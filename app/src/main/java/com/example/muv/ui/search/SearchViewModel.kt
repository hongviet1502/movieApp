package com.example.muv.ui.search

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
class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val searchHistoryManager: SearchHistoryManager
) : BaseViewModel() {

    // Search suggestions
    private val _searchSuggestions = MutableLiveData<List<String>>()
    val searchSuggestions: LiveData<List<String>> = _searchSuggestions

    // Recent searches
    private val _recentSearches = MutableLiveData<List<String>>()
    val recentSearches: LiveData<List<String>> = _recentSearches

    // Quick filters
    private val _quickFilters = MutableLiveData<List<SearchFilter>>()
    val quickFilters: LiveData<List<SearchFilter>> = _quickFilters

    // Popular movies (for initial screen)
    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    // Search results
    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    // Current search query
    private val _currentQuery = MutableLiveData<String>()
    val currentQuery: LiveData<String> = _currentQuery

    // Search specific loading state
    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching

    // Search configuration
    private var currentSortBy = SearchSortBy.POPULARITY
    private var appliedFilters = mutableListOf<SearchFilter>()

    init {
        loadRecentSearches()
        loadQuickFilters()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            try {
                showLoading()

                // Load popular movies for initial screen
                movieRepository.getPopularMovies().collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _popularMovies.value = resource.data?.take(10) ?: emptyList()
                            hideLoading()
                        }
                        is Resource.Error -> {
                            showError(resource.message ?: "Failed to load popular movies")
                            hideLoading()
                        }
                        is Resource.Loading -> {
                            // Loading handled by base
                        }
                    }
                }
            } catch (e: Exception) {
                showError("Error loading initial data: ${e.message}")
                hideLoading()
            }
        }
    }

    fun searchSuggestions(query: String) {
        if (query.length < 2) {
            _searchSuggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                // Generate suggestions based on query
                val suggestions = generateSearchSuggestions(query)
                _searchSuggestions.value = suggestions
            } catch (e: Exception) {
                _searchSuggestions.value = emptyList()
            }
        }
    }

    fun searchMovies(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }

        _currentQuery.value = query

        viewModelScope.launch {
            try {
                _isSearching.value = true

                movieRepository.searchMovies(query).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val results = resource.data ?: emptyList()
                            val sortedResults = applySortingAndFiltering(results)
                            _searchResults.value = sortedResults
                            _isSearching.value = false
                        }
                        is Resource.Error -> {
                            showError(resource.message ?: "Search failed")
                            _isSearching.value = false
                        }
                        is Resource.Loading -> {
                            // Loading handled by isSearching
                        }
                    }
                }
            } catch (e: Exception) {
                showError("Search error: ${e.message}")
                _isSearching.value = false
            }
        }
    }

    fun performSearch(query: String) {
        // Add to search history
        searchHistoryManager.addSearchQuery(query)
        loadRecentSearches()

        // Perform search
        searchMovies(query)
    }

    fun applyFilter(filter: SearchFilter) {
        if (appliedFilters.contains(filter)) {
            appliedFilters.remove(filter)
        } else {
            appliedFilters.add(filter)
        }

        // Update filter state
        updateQuickFilters()

        // Reapply search with filters
        _currentQuery.value?.let { query ->
            if (query.isNotEmpty()) {
                searchMovies(query)
            }
        }
    }

    fun applySorting(sortBy: SearchSortBy) {
        currentSortBy = sortBy

        // Reapply search with new sorting
        _currentQuery.value?.let { query ->
            if (query.isNotEmpty()) {
                searchMovies(query)
            }
        }
    }

    fun loadPopularMovies() {
        viewModelScope.launch {
            try {
                _isSearching.value = true

                movieRepository.getPopularMovies().collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _searchResults.value = resource.data ?: emptyList()
                            _currentQuery.value = ""
                            _isSearching.value = false
                        }
                        is Resource.Error -> {
                            showError(resource.message ?: "Failed to load popular movies")
                            _isSearching.value = false
                        }
                        is Resource.Loading -> {
                            // Loading handled by isSearching
                        }
                    }
                }
            } catch (e: Exception) {
                showError("Error loading popular movies: ${e.message}")
                _isSearching.value = false
            }
        }
    }

    fun deleteRecentSearch(query: String) {
        searchHistoryManager.removeSearchQuery(query)
        loadRecentSearches()
    }

    fun clearSearchHistory() {
        searchHistoryManager.clearHistory()
        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        val recent = searchHistoryManager.getRecentSearches()
        _recentSearches.value = recent
    }

    private fun loadQuickFilters() {
        val filters = listOf(
            SearchFilter("Action", "28"),
            SearchFilter("Comedy", "35"),
            SearchFilter("Drama", "18"),
            SearchFilter("Horror", "27"),
            SearchFilter("Romance", "10749"),
            SearchFilter("Sci-Fi", "878"),
            SearchFilter("Thriller", "53")
        )
        _quickFilters.value = filters
    }

    private fun updateQuickFilters() {
        val currentFilters = _quickFilters.value ?: return
        val updatedFilters = currentFilters.map { filter ->
            filter.copy(isSelected = appliedFilters.contains(filter))
        }
        _quickFilters.value = updatedFilters
    }

    private fun generateSearchSuggestions(query: String): List<String> {
        // Simple suggestion generation - in real app, this could come from API
        val commonSuggestions = listOf(
            "action movies",
            "comedy films",
            "horror movies",
            "romantic comedies",
            "sci-fi films",
            "thriller movies",
            "adventure films",
            "animated movies",
            "documentary films",
            "drama series"
        )

        return commonSuggestions
            .filter { it.contains(query, ignoreCase = true) }
            .take(5)
            .plus(generateQueryVariations(query))
            .distinct()
            .take(8)
    }

    private fun generateQueryVariations(query: String): List<String> {
        return listOf(
            "$query movies",
            "$query films",
            "$query series",
            "best $query",
            "top $query"
        ).filter { it != query }
    }

    private fun applySortingAndFiltering(movies: List<Movie>): List<Movie> {
        var filteredMovies = movies

        // Apply genre filters
        if (appliedFilters.isNotEmpty()) {
            val selectedGenreIds = appliedFilters.map { it.value.toIntOrNull() }.filterNotNull()
            filteredMovies = filteredMovies.filter { movie ->
                movie.genreIds.any { genreId -> selectedGenreIds.contains(genreId) }
            }
        }

        // Apply sorting
        return when (currentSortBy) {
            SearchSortBy.POPULARITY -> filteredMovies.sortedByDescending { it.popularity }
            SearchSortBy.RATING -> filteredMovies.sortedByDescending { it.voteAverage }
            SearchSortBy.RELEASE_DATE -> filteredMovies.sortedByDescending { it.releaseDate }
            SearchSortBy.TITLE -> filteredMovies.sortedBy { it.title }
        }
    }
}

// Data classes for search functionality
data class SearchFilter(
    val name: String,
    val value: String,
    val isSelected: Boolean = false
)

enum class SearchSortBy {
    POPULARITY,
    RATING,
    RELEASE_DATE,
    TITLE
}

// Search History Manager
interface SearchHistoryManager {
    fun addSearchQuery(query: String)
    fun removeSearchQuery(query: String)
    fun getRecentSearches(): List<String>
    fun clearHistory()
}