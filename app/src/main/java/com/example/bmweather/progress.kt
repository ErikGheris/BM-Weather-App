package com.example.bmweather

import android.view.View

class progress {

    fun start(progressBar: View) {
        progressBar.visibility = View.VISIBLE
    }

    fun done(progressBar: View) {
        progressBar.visibility = View.INVISIBLE
    }

}