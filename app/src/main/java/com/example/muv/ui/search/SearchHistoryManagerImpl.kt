package com.example.muv.ui.search

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SearchHistoryManager {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val maxHistorySize = 10

    override fun addSearchQuery(query: String) {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return

        val currentHistory = getRecentSearches().toMutableList()

        // Remove if already exists
        currentHistory.remove(cleanQuery)

        // Add to beginning
        currentHistory.add(0, cleanQuery)

        // Limit size
        if (currentHistory.size > maxHistorySize) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        saveSearchHistory(currentHistory)
    }

    override fun removeSearchQuery(query: String) {
        val currentHistory = getRecentSearches().toMutableList()
        currentHistory.remove(query)
        saveSearchHistory(currentHistory)
    }

    override fun getRecentSearches(): List<String> {
        val historyJson = sharedPreferences.getString(KEY_SEARCH_HISTORY, null)
        return if (historyJson != null) {
            try {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson(historyJson, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .remove(KEY_SEARCH_HISTORY)
            .apply()
    }

    private fun saveSearchHistory(history: List<String>) {
        val historyJson = gson.toJson(history)
        sharedPreferences.edit()
            .putString(KEY_SEARCH_HISTORY, historyJson)
            .apply()
    }

    companion object {
        private const val PREF_NAME = "search_history_prefs"
        private const val KEY_SEARCH_HISTORY = "search_history"
    }
}