package com.example.bmweather.openweathermap.response


data class Weather(
    var description: String,
    var icon: String,
    var id: Int,
    var main: String
)