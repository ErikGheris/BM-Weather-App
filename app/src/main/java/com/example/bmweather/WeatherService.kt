package com.example.bmweather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface WeatherService {
    @GET("data/2.5/weather?")
    // Insert parameters (q for city, units, lang to select the language, APPID set the api Key)
    fun getCurrentWeatherData(@Query("q") q: String, @Query("units") units: String, @Query("lang") lang: String, @Query("APPID") app_id: String): Call<WeatherResponse>
}