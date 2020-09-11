package com.example.bmweather

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.bmweather.adapter.DailyArrayAdapter
import com.example.bmweather.databinding.ActivitySecondBinding
import com.example.bmweather.openweathermap.FetchWeatherData
import com.example.bmweather.openweathermap.load
import com.example.bmweather.openweathermap.response.Daily
import kotlinx.android.synthetic.main.activity_second.*

lateinit var binding: ActivitySecondBinding
const val apiKey = "6133b390a077c487bc9ac43311b3ba26"
var units = "metric"
var lang = "de"
var exclude = "hourly,minutely"
var longitude: String = ""
var latitude: String = ""
val fetchWeather = FetchWeatherData

class SecondActivity : AppCompatActivity() {

    override fun onStart() {
        makeScreenUntouchable()
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getDeliveredData()
        Log.i("SecAct", "$longitude  und $latitude")
        super.onStart()
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeForecastRequest()
        binding.secondActivityButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        getDeliveredData()
        Log.i("SecAct", "Resume: $longitude  und $latitude")
        makeScreenUntouchable()
        makeForecastRequest()
    }
/*
    override fun onRestart() {
        super.onRestart()
        getDeliveredData()
        Log.i("SecAct", "Restart: $longitude  und $latitude")
        makeScreenUntouchable()
        makeForecastRequest()

    }*/

    private fun makeScreenUntouchable() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }


    fun makeForecastRequest() {
        Log.i("SecAct", "making forecast request")
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

    private fun getDeliveredData() {
        latitude = intent.getStringExtra("xCoordination").toString().trim()
        longitude = intent.getStringExtra("yCoordination").toString().trim()
    }

    fun fetchDailyWeather(daily: List<Daily>) {
        dailylist.adapter =
            DailyArrayAdapter(
                this, R.layout.daily_item,
                daily as ArrayList<Daily>
            )
    }

    fun uiUtility() {
        load.done(binding.secondActivitySpinner)
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.forecast_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
         /*   android.R.id.home -> {
                finish()
                return true
            }*/
        }
        return super.onOptionsItemSelected(item)
    }
}