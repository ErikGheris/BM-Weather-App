package com.example.bmweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bmweather.databinding.ActivityMainBinding
import com.example.bmweather.response.Daily
import kotlinx.android.synthetic.main.activity_second.*
lateinit var binding: ActivityMainBinding



// Declare parameters for tge GET funktion
val app_id = "6133b390a077c487bc9ac43311b3ba26"
var cityName = "Berlin"
var units = "metric"
var lang = "de"
var lastCityCache = cityName
var searched: String = ""

var exclude = "hourly,minutely"



class SecondActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
      val xCoordination= intent.getStringExtra("xCoordination").toString().trim()
     val yCoordination= intent.getStringExtra("yCoordination").toString().trim()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)


        val fetchWeather = FetchWeatherData
         fetchWeather.getForeCastWeatherReport(app_id,lat= xCoordination, lon = yCoordination, lang = lang,units = units,exclude = exclude, activity =this )
            button_2.setOnClickListener {
                Toast.makeText(this, "$xCoordination  AND   $yCoordination", Toast.LENGTH_SHORT).show()
        }
            button.setOnClickListener(){
            finish()
        }
    }








    @ExperimentalUnsignedTypes
    fun temp1(weather: Daily) {
        textView3.text = getString(R.string.min_temp).plus(weather.    temp.min.toUInt()).plus("  ").plus(getString(R.string.max_temp)).plus(weather.temp?.max?.toUInt())
    }

    @ExperimentalUnsignedTypes
    fun temp2(weather: Daily) {
        textView4.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ").plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }

    @ExperimentalUnsignedTypes
    fun temp3(weather: Daily) {
        textView5.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ").plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }

    @ExperimentalUnsignedTypes
    fun temp4(weather: Daily) {
        textView6.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ").plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }

    @ExperimentalUnsignedTypes
    fun temp5(weather: Daily) {
        textView7.text = getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ").plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }



}