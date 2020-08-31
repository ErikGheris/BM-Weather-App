package com.example.bmweather.utility

import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.example.bmweather.openweathermap.FetchWeatherData.FetchWeatherData.TAG


class Search {
    fun get(autoCompleteTextView: AutoCompleteTextView): Editable = autoCompleteTextView.text
    fun getSearchInput(searchView: SearchView) =
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {


                Log.i("SEAR","Press querysubmit")

                val input = searchView.query


                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                Log.i("SEAR","Press querytextchange")
                return false
            }

        })


    fun get(view: TextView): String = view.text.toString()
}

