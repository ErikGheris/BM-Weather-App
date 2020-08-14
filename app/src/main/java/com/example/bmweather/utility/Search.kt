package com.example.bmweather.utility

import android.text.Editable
import android.widget.AutoCompleteTextView
import android.widget.TextView

class Search {
    fun get(autoCompleteTextView: AutoCompleteTextView): Editable = autoCompleteTextView.text
    fun get(view: TextView): String  = view.text.toString()
}