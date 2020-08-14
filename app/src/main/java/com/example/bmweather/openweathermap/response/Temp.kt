package com.example.bmweather.openweathermap.response


data class Temp(
    var day: Double,
    var eve: Double,
    var max: Double,
    var min: Double,
    var morn: Double,
    var night: Double
)