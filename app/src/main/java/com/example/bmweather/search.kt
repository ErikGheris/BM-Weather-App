package com.example.bmweather

import android.text.Editable
import android.widget.AutoCompleteTextView

class Search {
    // a function to read and return the input value of a given autoCompleteTextView
    fun get(autoCompleteTextView: AutoCompleteTextView): Editable = autoCompleteTextView.text
}