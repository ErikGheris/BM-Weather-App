package com.example.bmweather.openweathermap.response


import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("1h")
    var h: Double // 1
)