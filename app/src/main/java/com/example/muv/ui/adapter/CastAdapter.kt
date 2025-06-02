package com.example.muv.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.muv.R
import com.example.muv.data.model.CastMember
import com.example.muv.databinding.ItemCastMemberBinding

class CastAdapter(
    private val onCastClick: (CastMember) -> Unit
) : ListAdapter<CastMember, CastAdapter.CastViewHolder>(CastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CastViewHolder(binding, onCastClick)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CastViewHolder(
        private val binding: ItemCastMemberBinding,
        private val onCastClick: (CastMember) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cast: CastMember) {
            binding.apply {
                castNameTextView.text = cast.name
                characterNameTextView.text = cast.character

                // Load profile image with CircleCrop
                Glide.with(itemView.context)
                    .load(cast.profileUrl)
                    .placeholder(R.drawable.cast_placeholder)
                    .error(R.drawable.cast_placeholder)
                    .transform(CircleCrop())
                    .into(castProfileImageView)

                // Click listener with animation
                root.setOnClickListener {
                    animateClick {
                        onCastClick(cast)
                    }
                }
            }
        }

        private fun animateClick(action: () -> Unit) {
            binding.root.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
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

    class CastDiffCallback : DiffUtil.ItemCallback<CastMember>() {
        override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember): Boolean = oldItem == newItem
    }
}