package com.example.muv.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muv.R
import com.example.muv.data.model.Movie
import com.example.muv.databinding.FragmentSearchBinding
import com.example.muv.ui.adapter.PopularSearchesAdapter
import com.example.muv.ui.adapter.QuickFiltersAdapter
import com.example.muv.ui.adapter.RecentSearchesAdapter
import com.example.muv.ui.adapter.SearchResultsAdapter
import com.example.muv.ui.adapter.SearchSuggestionsAdapter
import com.example.muv.ui.navigation.NavigationManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    // Adapters
    private lateinit var suggestionsAdapter: SearchSuggestionsAdapter
    private lateinit var recentSearchesAdapter: RecentSearchesAdapter
    private lateinit var quickFiltersAdapter: QuickFiltersAdapter
    private lateinit var popularSearchesAdapter: PopularSearchesAdapter
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    // Search debounce job
    private var searchJob: Job? = null
    private val searchDebounceTime = 500L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchInput()
        setupRecyclerViews()
        setupInitialState()
        setupClickListeners()
        setupSearchListeners()
        setupObservers()

        // Load initial data
        viewModel.loadInitialData()

        // Check if there's an initial search query
        arguments?.getString(ARG_SEARCH_QUERY)?.let { query ->
            binding.searchEditText.setText(query)
            viewModel.searchMovies(query)
        }
    }

    private fun setupSearchInput() {
        binding.searchEditText.requestFocus()

        // Show keyboard
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupRecyclerViews() {
        // Search suggestions
        suggestionsAdapter = SearchSuggestionsAdapter(
            onSuggestionClick = { suggestion ->
                selectSearchSuggestion(suggestion)
            },
            onInsertClick = { suggestion ->
                insertSearchSuggestion(suggestion)
            }
        )
        binding.suggestionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = suggestionsAdapter
        }

        // Recent searches
        recentSearchesAdapter = RecentSearchesAdapter(
            onRecentClick = { query ->
                selectRecentSearch(query)
            },
            onDeleteClick = { query ->
                viewModel.deleteRecentSearch(query)
            }
        )
        binding.recentSearchesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentSearchesAdapter
        }

        // Quick filters
        quickFiltersAdapter = QuickFiltersAdapter { filter ->
            viewModel.applyFilter(filter)
        }
        binding.quickFiltersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = quickFiltersAdapter
        }

        // Popular searches
        popularSearchesAdapter = PopularSearchesAdapter { movie ->
            NavigationManager.navigateToMovieDetail(requireActivity(), movie)
        }
        binding.popularSearchesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = popularSearchesAdapter
        }

        // Search results
        searchResultsAdapter = SearchResultsAdapter { movie ->
            NavigationManager.navigateToMovieDetail(requireActivity(), movie)
        }
        binding.searchResultsRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = searchResultsAdapter
        }
    }

    private fun setupInitialState() {
        showRecentSearches()
        showPopularSearches()
    }

    private fun setupClickListeners() {
        // Clear button
        binding.clearButton.setOnClickListener {
            clearSearch()
        }

        // Filter button
        binding.filterButton.setOnClickListener {
            showFilterDialog()
        }

        // Clear history
        binding.clearHistoryTextView.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        // Sort by
        binding.sortByTextView.setOnClickListener {
            showSortDialog()
        }

        // Explore button (no results state)
        binding.exploreButton.setOnClickListener {
            viewModel.loadPopularMovies()
        }
    }

    private fun setupSearchListeners() {
        // Text change listener with debounce
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()

                // Show/hide clear button
                binding.clearButton.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE

                // Cancel previous search job
                searchJob?.cancel()

                if (query.isEmpty()) {
                    showDefaultState()
                    return
                }

                // Debounce search
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(searchDebounceTime)
                    if (query.length >= 2) {
                        viewModel.searchSuggestions(query)
                        viewModel.searchMovies(query)
                    }
                }
            }
        })

        // IME action listener
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupObservers() {
        // Search suggestions
        viewModel.searchSuggestions.observe(viewLifecycleOwner) { suggestions ->
            suggestionsAdapter.submitList(suggestions)
            if (suggestions.isNotEmpty()) {
                showSuggestions()
            }
        }

        // Recent searches
        viewModel.recentSearches.observe(viewLifecycleOwner) { recentSearches ->
            recentSearchesAdapter.submitList(recentSearches)
            binding.recentSearchesContainer.visibility =
                if (recentSearches.isNotEmpty()) View.VISIBLE else View.GONE
        }

        // Quick filters
        viewModel.quickFilters.observe(viewLifecycleOwner) { filters ->
            quickFiltersAdapter.submitList(filters)
        }

        // Popular searches
        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            popularSearchesAdapter.submitList(movies)
        }

        // Search results
        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            searchResultsAdapter.submitList(movies)
            updateResultsCount(movies.size)

            if (movies.isEmpty() && binding.searchEditText.text.toString().isNotEmpty()) {
                showNoResults()
            } else if (movies.isNotEmpty()) {
                showSearchResults()
            }
        }

        // Loading state
        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.loadingProgressBar.visibility = if (isSearching) View.VISIBLE else View.GONE
        }

        // Current query
        viewModel.currentQuery.observe(viewLifecycleOwner) { query ->
            if (query.isNotEmpty()) {
                binding.noResultsDescriptionTextView.text =
                    "No movies found for \"$query\". Try different keywords."
            }
        }

        // ViewModel states
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Handle base loading if needed
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showSnackbar(it)
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message?.let {
                showSnackbar(it)
            }
        }
    }

    private fun performSearch() {
        val query = binding.searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            hideKeyboard()
            viewModel.performSearch(query)
        }
    }

    private fun selectSearchSuggestion(suggestion: String) {
        binding.searchEditText.setText(suggestion)
        binding.searchEditText.setSelection(suggestion.length)
        performSearch()
    }

    private fun insertSearchSuggestion(suggestion: String) {
        binding.searchEditText.setText(suggestion)
        binding.searchEditText.setSelection(suggestion.length)
    }

    private fun selectRecentSearch(query: String) {
        binding.searchEditText.setText(query)
        binding.searchEditText.setSelection(query.length)
        performSearch()
    }

    private fun clearSearch() {
        binding.searchEditText.text.clear()
        showDefaultState()
    }

    private fun showDefaultState() {
        hideAllContainers()
        binding.recentSearchesContainer.visibility = View.VISIBLE
        binding.popularSearchesContainer.visibility = View.VISIBLE
    }

    private fun showSuggestions() {
        hideAllContainers()
        binding.suggestionsContainer.visibility = View.VISIBLE
        binding.quickFiltersContainer.visibility = View.VISIBLE
    }

    private fun showSearchResults() {
        hideAllContainers()
        binding.searchResultsContainer.visibility = View.VISIBLE
    }

    private fun showNoResults() {
        hideAllContainers()
        binding.noResultsContainer.visibility = View.VISIBLE
    }

    private fun showRecentSearches() {
        binding.recentSearchesContainer.visibility = View.VISIBLE
    }

    private fun showPopularSearches() {
        binding.popularSearchesContainer.visibility = View.VISIBLE
    }

    private fun hideAllContainers() {
        binding.apply {
            suggestionsContainer.visibility = View.GONE
            recentSearchesContainer.visibility = View.GONE
            quickFiltersContainer.visibility = View.GONE
            searchResultsContainer.visibility = View.GONE
            popularSearchesContainer.visibility = View.GONE
            noResultsContainer.visibility = View.GONE
        }
    }

    private fun updateResultsCount(count: Int) {
        binding.resultsCountTextView.text = when (count) {
            0 -> "No results found"
            1 -> "Found 1 result"
            else -> "Found $count results"
        }
    }

    private fun showFilterDialog() {
        // TODO: Implement filter bottom sheet
        showSnackbar("Filter options coming soon!")
    }

    private fun showSortDialog() {
        // TODO: Implement sort options
        showSnackbar("Sort options coming soon!")
    }

    private fun hideKeyboard() {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun showSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
    }

    companion object {
        private const val ARG_SEARCH_QUERY = "search_query"

        fun newInstance(searchQuery: String = ""): SearchFragment {
            return SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SEARCH_QUERY, searchQuery)
                }
            }
        }
    }
}