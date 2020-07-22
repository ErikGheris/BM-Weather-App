package com.example.bmweather.response


import com.google.gson.annotations.SerializedName

data class City(
    var coord: Coord,
    var country: String, // DE
    var id: Int, // 2886946
    var name: String, // Koblenz
    var population: Int, // 107319
    var sunrise: Int, // 1594265367
    var sunset: Int, // 1594323592
    var timezone: Int // 7200
)