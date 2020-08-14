package com.example.bmweather.openweathermap.response


import com.google.gson.annotations.SerializedName

data class WeatherReport(
    var current: Current,
    var daily: List<Daily>,
    var hourly: List<Hourly>,
    var lat: Double,
    var lon: Double,
    var timezone: String,
    @SerializedName("timezone_offset")
    var timezoneOffset: Int
)