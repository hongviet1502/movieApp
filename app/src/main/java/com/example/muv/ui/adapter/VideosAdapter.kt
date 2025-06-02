package com.example.muv.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.muv.R
import com.example.muv.data.model.Video
import com.example.muv.databinding.ItemVideoThumbnailBinding

class VideosAdapter(
    private val onVideoClick: (Video) -> Unit
) : ListAdapter<Video, VideosAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoThumbnailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VideoViewHolder(binding, onVideoClick)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VideoViewHolder(
        private val binding: ItemVideoThumbnailBinding,
        private val onVideoClick: (Video) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(video: Video) {
            binding.apply {
                // Set duration based on video type
                videoDurationTextView.text = getVideoDuration(video.type)

                // Load YouTube thumbnail
                Glide.with(itemView.context)
                    .load(video.thumbnailUrl)
                    .placeholder(R.drawable.placeholder_video)
                    .error(R.drawable.placeholder_video)
                    .transform(RoundedCorners(16))
                    .into(videoThumbnailImageView)

                // Click listener with animation
                root.setOnClickListener {
                    animateClick {
                        onVideoClick(video)
                    }
                }
            }
        }

        private fun getVideoDuration(type: String): String {
            return when (type.lowercase()) {
                "trailer" -> "2:30"
                "teaser" -> "1:15"
                "clip" -> "3:45"
                "featurette" -> "5:20"
                else -> "2:00"
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

    class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean = oldItem == newItem
    }
}
