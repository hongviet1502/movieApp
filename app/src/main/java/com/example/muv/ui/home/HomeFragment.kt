package com.example.muv.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muv.R
import com.example.muv.data.model.Movie
import com.example.muv.databinding.FragmentHomeBinding
import com.example.muv.ui.adapter.MovieAdapter
import com.example.muv.ui.navigation.NavigationManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    // Adapters
    private lateinit var popularMoviesAdapter: MovieAdapter
    private lateinit var topRatedMoviesAdapter: MovieAdapter
    private lateinit var nowPlayingMoviesAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupUI() {
        setupRecyclerViews()
        setupHeaderButtons()
    }

    private fun setupRecyclerViews() {
        // Popular Movies RecyclerView
        popularMoviesAdapter = MovieAdapter { movie ->
            onMovieClick(movie)
        }
        binding.popularMoviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularMoviesAdapter
            setHasFixedSize(true)
        }

        // Top Rated Movies RecyclerView
        topRatedMoviesAdapter = MovieAdapter { movie ->
            onMovieClick(movie)
        }
        binding.topRatedMoviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = topRatedMoviesAdapter
            setHasFixedSize(true)
        }

        // Now Playing Movies RecyclerView
        nowPlayingMoviesAdapter = MovieAdapter { movie ->
            onMovieClick(movie)
        }
        binding.nowPlayingMoviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = nowPlayingMoviesAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupHeaderButtons() {
        // Profile button click
        binding.profileImageView.setOnClickListener {
            // Navigate to profile
            showSnackbar("Profile clicked")
        }

        // Notification button click
        binding.notificationImageView.setOnClickListener {
            // Navigate to notifications
            showSnackbar("Notifications clicked")
        }
    }

    private fun setupObservers() {
        // Loading state
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Error state
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                showError(error)
            } else {
                hideError()
            }
        }

        // Popular Movies
        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            popularMoviesAdapter.submitList(movies)
        }

        // Top Rated Movies
        viewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
            topRatedMoviesAdapter.submitList(movies)
        }

        // Now Playing Movies
        viewModel.nowPlayingMovies.observe(viewLifecycleOwner) { movies ->
            nowPlayingMoviesAdapter.submitList(movies)
        }

        // Featured Movie
//        viewModel.featuredMovie.observe(viewLifecycleOwner) { movie ->
//            movie?.let { setupFeaturedMovie(it) }
//        }
    }

    private fun setupListeners() {
        // Featured movie buttons
        binding.playButton.setOnClickListener {
//            viewModel.featuredMovie.value?.let { movie ->
//                playMovie(movie)
//            }
        }

        binding.myListButton.setOnClickListener {
//            viewModel.featuredMovie.value?.let { movie ->
//                addToMyList(movie)
//            }
        }

        // Retry button
        binding.retryButton.setOnClickListener {
            viewModel.loadAllMovies()
        }

        // Pull to refresh (if you want to add SwipeRefreshLayout)
        // You can wrap the NestedScrollView with SwipeRefreshLayout
    }

    private fun setupFeaturedMovie(movie: Movie) {
        binding.apply {
            // Load backdrop image
            Glide.with(requireContext())
                .load(movie.backdropUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .into(featuredBackdropImageView)

            // Set movie details
            featuredTitleTextView.text = movie.title
            featuredDescriptionTextView.text = movie.overview

            // Show featured movie section
            featuredMovieCard.visibility = View.VISIBLE
        }
    }

    private fun onMovieClick(movie: Movie) {
        showSnackbar("Clicked on: ${movie.title}")
        val moviePoster = binding.featuredMovieCard // Or get the clicked poster view
        NavigationManager.navigateToMovieDetail(
            requireActivity(),
            movie,
            moviePoster // For shared element transition
        )
    }

    private fun playMovie(movie: Movie) {
        // Handle play movie
        showSnackbar("Playing: ${movie.title}")

        // Navigate to player or show trailer
        // You can implement video player here
    }

    private fun addToMyList(movie: Movie) {
        // Add movie to user's list
        showSnackbar("Added ${movie.title} to My List")

        // Update button state
        binding.myListButton.text = "✓ In My List"

        // You can call viewModel.addToMyList(movie) here
    }

    private fun showError(message: String) {
        binding.errorContainer.visibility = View.VISIBLE
        binding.errorTextView.text = message

        // Hide other content
        binding.featuredMovieCard.visibility = View.GONE
    }

    private fun hideError() {
        binding.errorContainer.visibility = View.GONE
        binding.featuredMovieCard.visibility = View.VISIBLE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}