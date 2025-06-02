package com.example.muv.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.muv.databinding.ItemRecentSearchBinding

class RecentSearchesAdapter(
    private val onRecentClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<String, RecentSearchesAdapter.RecentSearchViewHolder>(RecentSearchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = ItemRecentSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecentSearchViewHolder(binding, onRecentClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecentSearchViewHolder(
        private val binding: ItemRecentSearchBinding,
        private val onRecentClick: (String) -> Unit,
        private val onDeleteClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(query: String) {
            binding.apply {
                recentSearchTextView.text = query

                root.setOnClickListener {
                    onRecentClick(query)
                }

                deleteButton.setOnClickListener {
                    onDeleteClick(query)
                }
            }
        }
    }

    class RecentSearchDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}
