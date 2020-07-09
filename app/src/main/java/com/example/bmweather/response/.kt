package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class WeatherResponse (
    var clouds: Clouds,
    var dt: Int, // 1594296000
    @SerializedName("dt_txt")
    var dtTxt: String, // 2020-07-09 12:00:00
    var main: Main,
    var rain: Rain,
    var sys: Sys,
    var weather: List<Weather>,
    var wind: Wind
)