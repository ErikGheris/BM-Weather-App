package com.example.bmweather.utility

import android.view.View

class Load {

    fun start(progressBar: View) {
        progressBar.visibility = View.VISIBLE
    }

    fun done(progressBar: View) {
        progressBar.visibility = View.INVISIBLE
    }

}