package com.example.bmweather.ResponseModel.current


import com.google.gson.annotations.SerializedName

data class Weather(
    var description: String, // clear sky
    var icon: String, // 01d
    var id: Int, // 800
    var main: String // Clear
)