package com.example.bmweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bmweather.databinding.ActivitySecondBinding
import com.example.bmweather.response.Daily
import kotlinx.android.synthetic.main.activity_second.*

lateinit var binding: ActivitySecondBinding
val app_id = "6133b390a077c487bc9ac43311b3ba26"
var cityName = "Berlin"
var units = "metric"
var lang = "de"
var lastCityCache = cityName
var searched: String = ""

var exclude = "hourly,minutely"


class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val xCoordination = intent.getStringExtra("xCoordination").toString().trim()
        val yCoordination = intent.getStringExtra("yCoordination").toString().trim()
        binding = ActivitySecondBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fetchWeather = FetchWeatherData
        binding.forecastButton.setOnClickListener {
            fetchWeather.getForeCastWeatherReport(
                app_id = app_id,
                lat = xCoordination,
                lon = yCoordination,
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