package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    var h: Double // 0.11
)