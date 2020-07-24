package com.example.bmweather.Network

import com.example.bmweather.ResponseModel.current.WeatherReport
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import source.open.akash.mvvmlogin.Model.nextdayforecast.WeatherNextDaysReport


interface WeatherService {
    @GET("data/2.5/weather?")
    // Insert parameters (q for city, units, lang to select the language, APPID set the api Key)
    fun getCurrentWeatherData(
        @Query("q") q: String,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("APPID") app_id: String
    ): Call<WeatherReport>

    @GET("data/2.5/forecast?")
    // Insert parameters (q for city, units, lang to select the language, APPID set the api Key)
    fun getForecastWeatherData(
        @Query("q") q: String,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("APPID") app_id: String,
        @Query("cnt") cnt: String
    ): Call<WeatherNextDaysReport>

    @GET("data/2.5/weather?")
    // Insert parameters (q for city, units, lang to select the language, APPID set the api Key)
    fun getCurrentLocationWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("APPID") app_id: String
    ): Call<WeatherReport>

    @GET("data/2.5/forecast?")
    // Insert parameters (q for city, units, lang to select the language, APPID set the api Key)
    fun getCurrentLocationForecastWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("lang") lang: String,
        @Query("APPID") app_id: String,
        @Query("cnt") cnt: String
    ): Call<WeatherNextDaysReport>
}

