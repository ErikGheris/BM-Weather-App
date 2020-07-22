package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class Weather(
    var description: String, // Leichter Regen
    var icon: String, // 10d
    var id: Int, // 500
    var main: String // Rain 1594382400
)