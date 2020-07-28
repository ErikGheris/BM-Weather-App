package com.example.bmweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bmweather.databinding.ActivitySecondBinding
import com.example.bmweather.response.Daily
import kotlinx.android.synthetic.main.activity_second.*

lateinit var binding: ActivitySecondBinding
const val apiKey = "6133b390a077c487bc9ac43311b3ba26"
var cityName = "Berlin"
var units = "metric"
var lang = "de"
var lastCityCache = cityName
var searched: String = ""
var exclude = "hourly,minutely"
var longitude: String = ""
var latitude: String = ""

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        latitude = intent.getStringExtra("xCoordination").toString().trim()
        longitude = intent.getStringExtra("yCoordination").toString().trim()
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fetchWeather = FetchWeatherData
        binding.forecastButton.setOnClickListener {
            fetchWeather.getForeCastWeatherReport(
                app_id = apiKey,
                lat = latitude,
                lon = longitude,
                lang = lang,
                units = units,
                exclude = exclude,
                activity = this
            )
        }
        binding.backButton.setOnClickListener() {
            finish()
        }
    }


    @ExperimentalUnsignedTypes
    fun temp1(weather: Daily) {
        textView3.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ")
            .plus(getString(R.string.max_temp)).plus(weather.temp?.max?.toUInt())
    }

    @ExperimentalUnsignedTypes
    fun temp2(weather: Daily) {
        textView4.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ")
            .plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }

    @ExperimentalUnsignedTypes
    fun temp3(weather: Daily) {
        textView5.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ")
            .plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }

    @ExperimentalUnsignedTypes
    fun temp4(weather: Daily) {
        textView6.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ")
            .plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }

    @ExperimentalUnsignedTypes
    fun temp5(weather: Daily) {
        textView7.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ")
            .plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }


}