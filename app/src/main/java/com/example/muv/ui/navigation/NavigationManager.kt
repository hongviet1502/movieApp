package com.example.muv.ui.navigation

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.example.muv.data.model.Movie
import com.example.muv.ui.DetailActivity
import com.example.muv.ui.FavoriteActivity
import com.example.muv.ui.ProfileActivity
import com.example.muv.ui.SearchActivity
import com.example.muv.ui.VideoPlayerActivity

object NavigationManager {

    /**
     * Navigate to movie detail with shared element transition
     */
    fun navigateToMovieDetail(
        activity: Activity,
        movie: Movie,
        sharedView: View? = null
    ) {
        val intent = Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.id)
            putExtra(DetailActivity.EXTRA_MOVIE_TITLE, movie.title)
            putExtra(DetailActivity.EXTRA_MOVIE_POSTER, movie.posterPath)
        }

        if (sharedView != null) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                sharedView,
                ViewCompat.getTransitionName(sharedView) ?: "movie_poster"
            )
            activity.startActivity(intent, options.toBundle())
        } else {
            activity.startActivity(intent)
            activity.overridePendingTransition(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }
    }

    /**
     * Navigate to movie detail by ID only (for similar movies)
     */
    fun navigateToMovieDetailById(
        activity: Activity,
        movieId: Int
    ) {
        val intent = Intent(activity, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_MOVIE_ID, movieId)
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    /**
     * Navigate to video player (fullscreen)
     */
    fun navigateToVideoPlayer(
        activity: Activity,
        videoKey: String,
        movieTitle: String,
        videoType: String = "Trailer"
    ) {
        val intent = Intent(activity, VideoPlayerActivity::class.java).apply {
            putExtra(VideoPlayerActivity.EXTRA_VIDEO_KEY, videoKey)
            putExtra(VideoPlayerActivity.EXTRA_VIDEO_TITLE, movieTitle)
            putExtra(VideoPlayerActivity.EXTRA_VIDEO_TYPE, videoType)
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    /**
     * Navigate to video player with YouTube URL
     */
    fun navigateToVideoPlayerWithUrl(
        activity: Activity,
        videoUrl: String,
        movieTitle: String
    ) {
        val intent = Intent(activity, VideoPlayerActivity::class.java).apply {
//            putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, videoUrl)
//            putExtra(VideoPlayerActivity.EXTRA_MOVIE_TITLE, movieTitle)
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    /**
     * Navigate to search with animation
     */
    fun navigateToSearch(activity: Activity, searchQuery: String = "") {
        val intent = Intent(activity, SearchActivity::class.java).apply {
            if (searchQuery.isNotEmpty()) {
//                putExtra(SearchActivity.EXTRA_SEARCH_QUERY, searchQuery)
            }
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    /**
     * Navigate to favorites
     */
    fun navigateToFavorites(activity: Activity) {
        val intent = Intent(activity, FavoriteActivity::class.java)
        activity.startActivity(intent)
        activity.overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    /**
     * Navigate to profile/settings
     */
    fun navigateToProfile(activity: Activity) {
        val intent = Intent(activity, ProfileActivity::class.java)
        activity.startActivity(intent)
        activity.overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    /**
     * Navigate to cast/person detail
     */
    fun navigateToPersonDetail(
        activity: Activity,
        personId: Int,
        personName: String,
        profilePath: String? = null
    ) {
        // TODO: Implement PersonDetailActivity
        // val intent = Intent(activity, PersonDetailActivity::class.java).apply {
        //     putExtra(PersonDetailActivity.EXTRA_PERSON_ID, personId)
        //     putExtra(PersonDetailActivity.EXTRA_PERSON_NAME, personName)
        //     putExtra(PersonDetailActivity.EXTRA_PROFILE_PATH, profilePath)
        // }
        // activity.startActivity(intent)
        // activity.overridePendingTransition(
        //     android.R.anim.slide_in_left,
        //     android.R.anim.slide_out_right
        // )
    }

    /**
     * Navigate to all reviews
     */
    fun navigateToAllReviews(
        activity: Activity,
        movieId: Int,
        movieTitle: String
    ) {
        // TODO: Implement ReviewsActivity
        // val intent = Intent(activity, ReviewsActivity::class.java).apply {
        //     putExtra(ReviewsActivity.EXTRA_MOVIE_ID, movieId)
        //     putExtra(ReviewsActivity.EXTRA_MOVIE_TITLE, movieTitle)
        // }
        // activity.startActivity(intent)
        // activity.overridePendingTransition(
        //     android.R.anim.slide_in_left,
        //     android.R.anim.slide_out_right
        // )
    }

    /**
     * Navigate back with animation
     */
    fun navigateBack(activity: Activity) {
        activity.finish()
        activity.overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    /**
     * Navigate back to home and clear task
     */
    fun navigateBackToHome(activity: Activity) {
        activity.finish()
        activity.overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }

    // Constants for extras
    object Extras {
        // Movie Detail
        const val EXTRA_MOVIE_ID = "extra_movie_id"
        const val EXTRA_MOVIE_TITLE = "extra_movie_title"
        const val EXTRA_MOVIE_POSTER = "extra_movie_poster"

        // Video Player
        const val EXTRA_VIDEO_KEY = "extra_video_key"
        const val EXTRA_VIDEO_URL = "extra_video_url"
        const val EXTRA_VIDEO_TYPE = "extra_video_type"

        // Search
        const val EXTRA_SEARCH_QUERY = "extra_search_query"

        // Person Detail
        const val EXTRA_PERSON_ID = "extra_person_id"
        const val EXTRA_PERSON_NAME = "extra_person_name"
        const val EXTRA_PROFILE_PATH = "extra_profile_path"

        // Reviews
        const val EXTRA_REVIEWS_MOVIE_ID = "extra_reviews_movie_id"
        const val EXTRA_REVIEWS_MOVIE_TITLE = "extra_reviews_movie_title"
    }
}