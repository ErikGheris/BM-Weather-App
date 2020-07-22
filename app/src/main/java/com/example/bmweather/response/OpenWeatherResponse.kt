package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
    var city: City,
    var cnt: Int, // 40
    var cod: String, // 200

    var message: Int // 0
)