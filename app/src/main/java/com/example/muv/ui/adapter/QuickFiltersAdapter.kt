package com.example.muv.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.muv.databinding.ItemFilterChipBinding
import com.example.muv.ui.search.SearchFilter

class QuickFiltersAdapter(
    private val onFilterClick: (SearchFilter) -> Unit
) : ListAdapter<SearchFilter, QuickFiltersAdapter.FilterViewHolder>(FilterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = ItemFilterChipBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FilterViewHolder(binding, onFilterClick)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FilterViewHolder(
        private val binding: ItemFilterChipBinding,
        private val onFilterClick: (SearchFilter) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(filter: SearchFilter) {
            binding.apply {
                root.text = filter.name
                root.isChecked = filter.isSelected

                root.setOnClickListener {
                    onFilterClick(filter)
                }
            }
        }
    }

    class FilterDiffCallback : DiffUtil.ItemCallback<SearchFilter>() {
        override fun areItemsTheSame(oldItem: SearchFilter, newItem: SearchFilter): Boolean =
            oldItem.value == newItem.value
        override fun areContentsTheSame(oldItem: SearchFilter, newItem: SearchFilter): Boolean =
            oldItem == newItem
    }
}