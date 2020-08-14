package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class Hourly(
    var clouds: Int, // 46
    @SerializedName("dew_point")
    var dewPoint: Double, // 17.16
    var dt: Int, // 1597212000
    @SerializedName("feels_like")
    var feelsLike: Double, // 21.76
    var humidity: Int, // 82
    var pop: Double, // 0.2
    var pressure: Int, // 1017
    var rain: Rain,
    var temp: Double, // 20.33
    var visibility: Int, // 10000
    var weather: List<WeatherXX>,
    @SerializedName("wind_deg")
    var windDeg: Int, // 68
    @SerializedName("wind_speed")
    var windSpeed: Double // 1.45
)