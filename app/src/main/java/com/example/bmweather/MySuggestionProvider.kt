package com.example.bmweather

import android.content.SearchRecentSuggestionsProvider
import android.provider.SearchRecentSuggestions


class MySuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.example.bmweather.MySuggestionProvider"
        const val MODE: Int = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES

    }


}