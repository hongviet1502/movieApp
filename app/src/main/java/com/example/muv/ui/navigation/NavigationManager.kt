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
//            putExtra(DetailActivity.EXTRA_MOVIE, movie)
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
     * Navigate to video player (fullscreen)
     */
    fun navigateToVideoPlayer(
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
     * Navigate back with animation
     */
    fun navigateBack(activity: Activity) {
        activity.finish()
        activity.overridePendingTransition(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
    }
}