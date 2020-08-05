package com.example.bmweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.bmweather.adapter.DailyArrayAdapter
import com.example.bmweather.databinding.ActivitySecondBinding
import com.example.bmweather.response.Daily
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_second.*
import java.text.SimpleDateFormat

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
        binding.swipeforecast.setOnRefreshListener {
            fetchWeather.getForeCastWeatherReport(
                app_id = apiKey,
                lat = latitude,
                lon = longitude,
                lang = lang,
                units = units,
                exclude = exclude,
                activity = this
            )
            Toast.makeText(this, "$latitude   $longitude", Toast.LENGTH_SHORT).show()
        }
        binding.fragment2.setOnClickListener() {
            finish()
        }
    }
    fun fetchDailyWeather(daily: List<Daily>){ dailylist.adapter=
        let {
            DailyArrayAdapter(
                it,R.layout.daily_item,
                daily as ArrayList<Daily>
            )
        }

    }


}