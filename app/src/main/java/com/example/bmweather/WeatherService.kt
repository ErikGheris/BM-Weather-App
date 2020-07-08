package com.example.bmweather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface WeatherService {
    @GET("data/2.5/weather?")
    //fun getCurrentWeatherData(@Query("lat") lat: String, @Query("lon") lon: String, @Query("APPID") app_id: String): Call<WeatherResponse>
    fun getCurrentWeatherData(@Query("q") q: String, @Query("units") units: String, @Query("lang") lang: String, @Query("APPID") app_id: String): Call<WeatherResponse>
}