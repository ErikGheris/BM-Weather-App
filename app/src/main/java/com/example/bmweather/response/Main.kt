package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("feels_like")
    var feelsLike: Double, // 20.79
    @SerializedName("grnd_level")
    var grndLevel: Int, // 1007
    var humidity: Int, // 61
    var pressure: Int, // 1012
    @SerializedName("sea_level")
    var seaLevel: Int, // 1014
    var temp: Double, // 22.93
    @SerializedName("temp_kf")
    var tempKf: Double, // -2.01
    @SerializedName("temp_max")
    var tempMax: Double, // 24.94
    @SerializedName("temp_min")
    var tempMin: Double // 22.93
)