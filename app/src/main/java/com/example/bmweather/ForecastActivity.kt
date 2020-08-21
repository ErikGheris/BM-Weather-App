package com.example.bmweather

import androidx.appcompat.app.AppCompatActivity
import com.example.bmweather.adapter.DailyArrayAdapter
import com.example.bmweather.databinding.ActivitySecondBinding
import com.example.bmweather.openweathermap.FetchWeatherData
import com.example.bmweather.openweathermap.response.Daily
import kotlinx.android.synthetic.main.activity_second.*

lateinit var binding: ActivitySecondBinding
const val apiKey = "6133b390a077c487bc9ac43311b3ba26"
var units = "metric"
var lang = "de"
var exclude = "hourly,minutely"
var longitude: String = ""
var latitude: String = ""

class SecondActivity : AppCompatActivity() {
    
    override fun onStart() {
        latitude = intent.getStringExtra("xCoordination").toString().trim()
        longitude = intent.getStringExtra("yCoordination").toString().trim()
        super.onStart()
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fetchWeather = FetchWeatherData
        fetchWeather.getForeCastWeatherReport(
            app_id = apiKey,
            lat = latitude,
            lon = longitude,
            lang = lang,
            units = units,
            exclude = exclude,
            activity = this
        )
        fragment2.setOnClickListener {
            finish()
        }
    }
    fun fetchDailyWeather(daily: List<Daily>){ dailylist.adapter=
        DailyArrayAdapter(
            this,R.layout.daily_item,
            daily as ArrayList<Daily>
        )

    }

    fun uiUtility(){
        load.done(binding.secondActivitySpinner)
    }
}