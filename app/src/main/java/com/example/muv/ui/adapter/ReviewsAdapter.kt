package com.example.muv.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.muv.R
import com.example.muv.data.model.Review
import com.example.muv.databinding.ItemReviewBinding

class ReviewsAdapter(
    private val onReviewClick: (Review) -> Unit
) : ListAdapter<Review, ReviewsAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReviewViewHolder(binding, onReviewClick)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        Log.i("ReviewsAdapter", "onBindViewHolder: " +  getItem(position))
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(
        private val binding: ItemReviewBinding,
        private val onReviewClick: (Review) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.apply {
                usernameTextView.text = review.author
                reviewContentTextView.text = review.shortContent
                reviewDateTextView.text = formatDate(review.createdAt)

                // Set rating if available
                review.rating?.let { rating ->
                    reviewRatingTextView.text = String.format("%.1f", rating)
                    reviewRatingTextView.visibility = android.view.View.VISIBLE
                } ?: run {
                    reviewRatingTextView.visibility = android.view.View.GONE
                }

                // Load user avatar
                loadUserAvatar(review.url.toString())

                // Handle expand/collapse functionality
                setupExpandCollapse(review)

                // Click listener
                root.setOnClickListener {
                    onReviewClick(review)
                }
            }
        }

        private fun loadUserAvatar(avatarUrl: String) {
            if (avatarUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.cast_placeholder)
                    .error(R.drawable.cast_placeholder)
                    .transform(CircleCrop())
                    .into(binding.userAvatarImageView)
            } else {
                binding.userAvatarImageView.setImageResource(R.drawable.cast_placeholder)
            }
        }

        private fun setupExpandCollapse(review: Review) {
            if (review.content.length > 200) {
                binding.expandReviewTextView.visibility = android.view.View.VISIBLE
                binding.expandReviewTextView.setOnClickListener {
                    if (binding.reviewContentTextView.maxLines == 3) {
                        // Expand
                        binding.reviewContentTextView.maxLines = Int.MAX_VALUE
                        binding.reviewContentTextView.text = review.content
                        binding.expandReviewTextView.text = "Show less"
                    } else {
                        // Collapse
                        binding.reviewContentTextView.maxLines = 3
                        binding.reviewContentTextView.text = review.shortContent
                        binding.expandReviewTextView.text = "Read more"
                    }
                }
            } else {
                binding.expandReviewTextView.visibility = android.view.View.GONE
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
                val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "Unknown date"
            } catch (e: Exception) {
                // Try alternative format
                try {
                    val altFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                    val date = altFormat.parse(dateString)
                    date?.let { outputFormat.format(it) } ?: "Unknown date"
                } catch (e2: Exception) {
                    "Unknown date"
                }
            }
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem == newItem
    }
}