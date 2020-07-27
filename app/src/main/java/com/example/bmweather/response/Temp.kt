package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class Temp(
    var day: Double,
    var eve: Double,
    var max: Double,
    var min: Double,
    var morn: Double,
    var night: Double
)