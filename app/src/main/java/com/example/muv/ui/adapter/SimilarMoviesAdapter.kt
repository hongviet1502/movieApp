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

class SimilarMoviesAdapter(
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<Movie, SimilarMoviesAdapter.SimilarMovieViewHolder>(SimilarMovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarMovieViewHolder {
        val binding = ItemMovieHorizontalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SimilarMovieViewHolder(binding, onMovieClick)
    }

    override fun onBindViewHolder(holder: SimilarMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SimilarMovieViewHolder(
        private val binding: ItemMovieHorizontalBinding,
        private val onMovieClick: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                movieTitleTextView.text = movie.title
                movieRatingTextView.text = String.format("%.1f", movie.voteAverage)
//                movieYearTextView.text = movie.releaseYear

                // Load poster
                Glide.with(itemView.context)
                    .load(movie.posterUrl)
                    .placeholder(R.drawable.placeholder_movie)
                    .error(R.drawable.placeholder_movie)
                    .transform(RoundedCorners(16))
                    .into(moviePosterImageView)

                // Hide favorite button for similar movies
                favoriteImageView.visibility = android.view.View.GONE

                // Click listener with animation
                root.setOnClickListener {
                    animateClick {
                        onMovieClick(movie)
                    }
                }
            }
        }

        private fun animateClick(action: () -> Unit) {
            binding.root.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    binding.root.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .withEndAction { action() }
                        .start()
                }
                .start()
        }
    }

    class SimilarMovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
    }
}