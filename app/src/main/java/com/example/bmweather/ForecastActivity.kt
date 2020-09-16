package com.example.bmweather

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.example.bmweather.adapter.DailyArrayAdapter
import com.example.bmweather.databinding.ActivitySecondBinding
import com.example.bmweather.openweathermap.FetchWeatherData
import com.example.bmweather.openweathermap.load
import com.example.bmweather.openweathermap.response.Daily
import com.example.bmweather.utility.Utility
import kotlinx.android.synthetic.main.activity_second.*

const val apiKey = "6133b390a077c487bc9ac43311b3ba26"
var units = "metric"
var lang = "de"
var exclude = "hourly,minutely"
var longitude: String = ""
var latitude: String = ""
val fetchWeather = FetchWeatherData

val myUtilities: Utility = Utility()

class SecondActivity : AppCompatActivity() {
    lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
        myUtilities.makeScreenUntouchable(window)
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        getDeliveredData()
        Log.i("SecAct", "$longitude  und $latitude")
        makeForecastRequest()
        binding.secondActivityButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        myUtilities.makeScreenUntouchable(window)
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
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}