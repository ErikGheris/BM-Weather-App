package com.example.bmweather.ResponseModel.current


import com.google.gson.annotations.SerializedName

data class Sys(
    var country: String, // DE
    var id: Int, // 19193
    var sunrise: Int, // 1595389458
    var sunset: Int, // 1595446048
    var type: Int // 3
)