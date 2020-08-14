package com.example.bmweather.openweathermap.response


import com.google.gson.annotations.SerializedName

data class Daily(
    var clouds: Int,
    @SerializedName("dew_point")
    var dewPoint: Double,
    var dt: Int,
    @SerializedName("feels_like")
    var feelsLike: FeelsLike,
    var humidity: Int,
    var pop: Double,
    var pressure: Int,
    var rain: Double,
    var sunrise: Int,
    var sunset: Int,
    var temp: Temp,
    var uvi: Double,
    var weather: List<WeatherX>,
    @SerializedName("wind_deg")
    var windDeg: Int,
    @SerializedName("wind_speed")
    var windSpeed: Double
)