package com.example.muv.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.muv.R
import com.example.muv.data.model.Movie
import com.example.muv.databinding.ItemMovieHorizontalBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding, onMovieClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(
        private val binding: ItemMovieHorizontalBinding,
        private val onMovieClick: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                // Set movie title
                movieTitleTextView.text = movie.title

                // Set movie rating
                movieRatingTextView.text = String.format("%.1f", movie.voteAverage)

                // Set release year
                movieYearTextView.text = getYearFromDate(movie.releaseDate)

                // Load poster image
                Glide.with(itemView.context)
                    .load(movie.posterUrl)
                    .placeholder(R.drawable.placeholder_movie)
                    .error(R.drawable.placeholder_movie)
                    .transform(RoundedCorners(16))
                    .into(moviePosterImageView)

                // Handle favorite button
                updateFavoriteButton(movie.isFavorite)
                favoriteImageView.setOnClickListener {
                    toggleFavorite(movie)
                }

                // Handle item click
                root.setOnClickListener {
                    onMovieClick(movie)
                }

                // Add click animation
                root.setOnTouchListener { view, event ->
                    when (event.action) {
                        android.view.MotionEvent.ACTION_DOWN -> {
                            view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                        }
                        android.view.MotionEvent.ACTION_UP,
                        android.view.MotionEvent.ACTION_CANCEL -> {
                            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                        }
                    }
                    false
                }
            }
        }

        private fun updateFavoriteButton(isFavorite: Boolean) {
            val iconRes = if (isFavorite) {
                R.drawable.ic_favorite_filled
            } else {
                R.drawable.ic_favorite_border
            }
            binding.favoriteImageView.setImageResource(iconRes)
        }

        private fun toggleFavorite(movie: Movie) {
            movie.isFavorite = !movie.isFavorite
            updateFavoriteButton(movie.isFavorite)

            // Add animation
            binding.favoriteImageView.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(150)
                .withEndAction {
                    binding.favoriteImageView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150)
                        .start()
                }
                .start()

            // You can add callback to ViewModel here
            // onFavoriteToggle(movie)
        }

        private fun getYearFromDate(dateString: String?): String {
            return try {
                if (dateString.isNullOrEmpty()) return "N/A"
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(dateString)
                val calendar = Calendar.getInstance()
                calendar.time = date ?: return "N/A"
                calendar.get(Calendar.YEAR).toString()
            } catch (e: Exception) {
                "N/A"
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}