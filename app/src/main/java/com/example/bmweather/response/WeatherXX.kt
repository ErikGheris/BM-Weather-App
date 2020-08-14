package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class WeatherXX(
    var description: String, // Mäßig bewölkt
    var icon: String, // 03d
    var id: Int, // 802
    var main: String // Clouds
)