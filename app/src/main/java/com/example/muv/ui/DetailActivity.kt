package com.example.muv.ui

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muv.R
import com.example.muv.data.model.CastMember
import com.example.muv.data.model.Movie
import com.example.muv.data.model.MovieDetail
import com.example.muv.data.model.Review
import com.example.muv.data.model.Video
import com.example.muv.databinding.ActivityDetailBinding
import com.example.muv.ui.adapter.CastAdapter
import com.example.muv.ui.adapter.GenresAdapter
import com.example.muv.ui.adapter.ReviewsAdapter
import com.example.muv.ui.adapter.SimilarMoviesAdapter
import com.example.muv.ui.adapter.VideosAdapter
import com.example.muv.ui.base.BaseActivity
import com.example.muv.ui.navigation.NavigationManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding, MovieDetailViewModel>() {

    override val layoutId: Int = R.layout.activity_detail
    override val viewModelClass: Class<MovieDetailViewModel> = MovieDetailViewModel::class.java

    // Adapters
    private lateinit var genresAdapter: GenresAdapter
    private lateinit var castAdapter: CastAdapter
    private lateinit var videosAdapter: VideosAdapter
    private lateinit var similarMoviesAdapter: SimilarMoviesAdapter
    private lateinit var reviewsAdapter: ReviewsAdapter

    private var movieId: Int = 0
    private var movieTitle: String = ""

    override fun shouldShowBackButton(): Boolean = true

    override fun initView() {
        // Extract intent data
        extractIntentData()

        if (movieId == 0) {
            handleError("Invalid movie ID")
            NavigationManager.navigateBack(this)
            return
        }

        setupUI()
        setupSharedElementTransition()
    }

    override fun initData() {
        // Load movie details
        viewModel.loadMovieDetails(movieId)
    }

    override fun initListener() {
        setupClickListeners()
    }

    override fun onRetryClicked() {
        viewModel.retryLoadMovieDetails()
    }

    private fun extractIntentData() {
        movieId = intent.getIntExtra(EXTRA_MOVIE_ID, 0)
        movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE) ?: ""

        // Set initial title if available
        if (movieTitle.isNotEmpty()) {
            binding.collapsingToolbar.title = movieTitle
        }
    }

    private fun setupUI() {
        setupToolbar()
        setupRecyclerViews()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            NavigationManager.navigateBack(this)
        }
    }

    private fun setupSharedElementTransition() {
        // Handle shared element transition for poster
        binding.posterImageView.transitionName = "movie_poster"

        // Load initial poster if available
        intent.getStringExtra(EXTRA_MOVIE_POSTER)?.let { posterPath ->
            val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"
            Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .into(binding.posterImageView)
        }
    }

    private fun setupRecyclerViews() {
        // Genres RecyclerView
        genresAdapter = GenresAdapter()
        binding.genresRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = genresAdapter
        }

        // Cast RecyclerView
        castAdapter = CastAdapter { castMember ->
            onCastMemberClick(castMember)
        }
        binding.castRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = castAdapter
        }

        // Videos RecyclerView
        videosAdapter = VideosAdapter { video ->
            onVideoClick(video)
        }
        binding.videosRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = videosAdapter
        }

        // Similar Movies RecyclerView
        similarMoviesAdapter = SimilarMoviesAdapter { movie ->
            onSimilarMovieClick(movie)
        }
        binding.similarMoviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = similarMoviesAdapter
        }

        // Reviews RecyclerView
        reviewsAdapter = ReviewsAdapter { review ->
            onReviewClick(review)
        }
        binding.reviewsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = reviewsAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        // Movie details
        viewModel.movieDetails.observe(this) { movie ->
            movie?.let { setupMovieDetails(it) }
        }

        // Cast
        viewModel.cast.observe(this) { cast ->
            castAdapter.submitList(cast)
        }

        // Videos
        viewModel.videos.observe(this) { videos ->
            videosAdapter.submitList(videos)
        }

        // Similar movies
        viewModel.similarMovies.observe(this) { movies ->
            similarMoviesAdapter.submitList(movies)
        }

        // Reviews
        viewModel.reviews.observe(this) { reviews ->
            reviewsAdapter.submitList(reviews.take(3)) // Show only first 3 reviews
        }

        // Favorite status
        viewModel.isFavorite.observe(this) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }

        // Custom loading state for this screen
        viewModel.loading.observe(this) { isLoading ->
            binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Custom error handling
        viewModel.error.observe(this) { error ->
            if (error != null) {
                showCustomError(error)
            } else {
                hideCustomError()
            }
        }
    }

    private fun setupClickListeners() {
        // Play button
        binding.playButton.setOnClickListener {
            playFirstTrailer()
        }

        // Play FAB
        binding.playFab.setOnClickListener {
            playFirstTrailer()
        }

        // My List button
        binding.myListButton.setOnClickListener {
            viewModel.toggleFavorite()
        }

        // See all reviews
        binding.seeAllReviewsTextView.setOnClickListener {
            NavigationManager.navigateToAllReviews(this, movieId, movieTitle)
        }

        // Retry button (for custom error state)
        binding.retryButton.setOnClickListener {
            viewModel.retryLoadMovieDetails()
        }
    }

    private fun setupMovieDetails(movie: MovieDetail) {
        Log.i("DetailActivity", "setupMovieDetails: $movie")
        binding.apply {
            // Set movie title
            collapsingToolbar.title = movie.title
            titleTextView.text = movie.title
            movieTitle = movie.title

            // Set basic info
            releaseDateTextView.text = "${movie.releaseYear} • ${movie.formattedRuntime}"
            ratingTextView.text = String.format("%.1f", movie.voteAverage)
            overviewTextView.text = movie.overview

            // Load images
            loadMovieImages(movie)

            // Set genres
            genresAdapter.submitList(movie.genres)
        }
    }

    private fun loadMovieImages(movie: MovieDetail) {
        // Load backdrop
        Glide.with(this)
            .load(movie.backdropUrl)
            .placeholder(R.drawable.placeholder_backdrop)
            .error(R.drawable.placeholder_backdrop)
            .into(binding.backdropImageView)

        // Load poster (if not already loaded from shared element)
        if (intent.getStringExtra(EXTRA_MOVIE_POSTER).isNullOrEmpty()) {
            Glide.with(this)
                .load(movie.posterUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .into(binding.posterImageView)
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.myListButton.text = if (isFavorite) "✓ In My List" else "+ My List"
    }

    private fun playFirstTrailer() {
        val trailer = viewModel.videos.value?.firstOrNull { it.isTrailer }
        Log.i(TAG, "playFirstTrailer: $trailer")
        if (trailer != null) {
            NavigationManager.navigateToVideoPlayer(this, trailer.key, movieTitle, trailer.type)
        } else {
            showMessage("No trailer available for $movieTitle")
        }
    }

    private fun onCastMemberClick(castMember: CastMember) {
        NavigationManager.navigateToPersonDetail(
            this,
            castMember.id,
            castMember.name,
            castMember.profilePath
        )
    }

    private fun onVideoClick(video: Video) {
        NavigationManager.navigateToVideoPlayer(this, video.key, video.name, video.type)
    }

    private fun onSimilarMovieClick(movie: Movie) {
        NavigationManager.navigateToMovieDetailById(this, movie.id)
    }

    private fun onReviewClick(review: Review) {
        // Expand review or navigate to full review
        showMessage("Review by: ${review.author}")
    }

    // Custom error handling for this screen (in addition to base class)
    private fun showCustomError(message: String) {
        binding.errorContainer.visibility = View.VISIBLE
        binding.errorTextView.text = message
    }

    private fun hideCustomError() {
        binding.errorContainer.visibility = View.GONE
    }

    // Override base loading handling to use custom progress bar
    override fun handleLoadingState(isLoading: Boolean) {
        // Use both base loading dialog and custom progress bar
        super.handleLoadingState(isLoading)
        binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val TAG = "DetailActivity"
        const val EXTRA_MOVIE_ID = "extra_movie_id"
        const val EXTRA_MOVIE_TITLE = "extra_movie_title"
        const val EXTRA_MOVIE_POSTER = "extra_movie_poster"
    }
}