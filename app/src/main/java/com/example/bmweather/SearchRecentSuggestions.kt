package com.example.bmweather

import android.content.ContentResolver
import android.content.Context
import android.provider.SearchRecentSuggestions
const val myMaxEntries = 3
class SearchRecentSuggestions(context: Context?, authority: String?, mode: Int) :
    SearchRecentSuggestions(context, authority, mode) {
    override fun truncateHistory(cr: ContentResolver?, maxEntries: Int) {
        super.truncateHistory(cr, myMaxEntries)
    }

    override fun clearHistory() {
        super.clearHistory()
    }
}