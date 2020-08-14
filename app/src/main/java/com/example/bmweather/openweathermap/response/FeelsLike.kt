package com.example.bmweather.openweathermap.response


data class FeelsLike(
    var day: Double,
    var eve: Double,
    var morn: Double,
    var night: Double
)