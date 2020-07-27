package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class FeelsLike(
    var day: Double,
    var eve: Double,
    var morn: Double,
    var night: Double
)