package com.example.bmweather.ResponseModel.current


import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("feels_like")
    var feelsLike: Double, // 11.16
    var humidity: Int, // 81
    var pressure: Int, // 1020
    var temp: Double, // 12.12
    @SerializedName("temp_max")
    var tempMax: Double, // 13.33
    @SerializedName("temp_min")
    var tempMin: Double // 10.56
)