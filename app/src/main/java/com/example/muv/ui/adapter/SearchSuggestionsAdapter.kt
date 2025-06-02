package com.example.muv.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.muv.databinding.ItemSearchSuggestionBinding

class SearchSuggestionsAdapter(
    private val onSuggestionClick: (String) -> Unit,
    private val onInsertClick: (String) -> Unit
) : ListAdapter<String, SearchSuggestionsAdapter.SuggestionViewHolder>(SuggestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding = ItemSearchSuggestionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SuggestionViewHolder(binding, onSuggestionClick, onInsertClick)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SuggestionViewHolder(
        private val binding: ItemSearchSuggestionBinding,
        private val onSuggestionClick: (String) -> Unit,
        private val onInsertClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(suggestion: String) {
            binding.apply {
                suggestionTextView.text = suggestion

                root.setOnClickListener {
                    onSuggestionClick(suggestion)
                }

                insertButton.setOnClickListener {
                    onInsertClick(suggestion)
                }
            }
        }
    }

    class SuggestionDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }
}