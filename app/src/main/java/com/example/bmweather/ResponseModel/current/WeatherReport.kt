package com.example.bmweather.ResponseModel.current


import com.google.gson.annotations.SerializedName

data class WeatherReport(
    var base: String, // stations
    var clouds: Clouds,
    var cod: Int, // 200
    var coord: Coord,
    var dt: Int, // 1595398770
    var id: Int, // 2886946
    var main: Main,
    var name: String, // Koblenz
    var sys: Sys,
    var timezone: Int, // 7200
    var visibility: Int, // 10000
    var weather: List<Weather>,
    var wind: Wind
)