package com.example.bmweather.network

import com.example.bmweather.response.WeatherReport
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {
    @GET("data/2.5/onecall?")
    // Insert parameters (q for city, units, lang to select the language, APPID set the api Key)
    fun getCurrentWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("APPID") app_id: String,
        @Query("exclude") exclude: String
    ): Call<WeatherReport>
}

