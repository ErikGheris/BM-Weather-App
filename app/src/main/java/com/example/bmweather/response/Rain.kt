package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("1h")
    var h: Double // 1
)