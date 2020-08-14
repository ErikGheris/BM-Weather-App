package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class Weather(
    var description: String,
    var icon: String,
    var id: Int,
    var main: String
)