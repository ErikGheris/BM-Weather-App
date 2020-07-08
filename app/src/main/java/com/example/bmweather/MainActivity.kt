package com.example.bmweather

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory




class MainActivity : AppCompatActivity() {

    private lateinit var weatherDescription: TextView
    private lateinit var mainTemp: TextView
    private lateinit var tempAllDay: TextView
    private lateinit var city: TextView
    private lateinit var realTemp: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toaster Message
        search_button.setOnClickListener {
            Toast.makeText(
                this, "this is a toast message",
                Toast.LENGTH_SHORT
            ).show()
        }

        // clears the autoCompleteTExtView when it is clicked
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.setText("")
        }
        //

        weatherDescription = findViewById(R.id.description)
        findViewById<View>(R.id.search_button).setOnClickListener { getCurrentData() }

        mainTemp = findViewById(R.id.mainTemp)

        tempAllDay = findViewById(R.id.TempAllDay)

        city = findViewById(R.id.city)

        realTemp = findViewById(R.id.realTemp)



    }




    internal fun getCurrentData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeatherData(q, units, lang, AppId)
        //val call = service.getCurrentWeatherData(lat, lon, AppId)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: retrofit2.Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.code() == 200) {
                    val weatherResponse = response.body()!!


                    val stringBuilder = null
                    weatherDescription.text = stringBuilder
                    rain(weatherResponse.weather[0])
                    temp(weatherResponse.main!!)
                    tempallday(weatherResponse.main!!)
                    realTemp(weatherResponse.main!!)
                    city(weatherResponse)
                }
            }


            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                weatherDescription.text = t.message
            }
        })
    }

    private fun rain(weather: Weather){
        weatherDescription.text = "Wetter: ".plus(weather.description)
    }

    private fun temp(main: Main){
        mainTemp.text = "".plus(main.temp).plus(" °C")
    }

    private fun tempallday(main: Main) {
        tempAllDay.text = "min: ".plus(main.temp_min).plus("  ").plus("max: ").plus(main.temp_max)
    }

    private fun realTemp(main: Main) {
        realTemp.text = "Gefühlt wie: ".plus(main.feels_like)
    }

    private fun city(weatherResponse: WeatherResponse) {
        city.text = weatherResponse.name.plus(", ").plus(weatherResponse.sys!!.country)
    }

    companion object {

        var BaseUrl = "http://api.openweathermap.org/"
        var AppId = "6133b390a077c487bc9ac43311b3ba26"
        //var lat = "50.3535700"
        //var lon = "7.5788300"
        var q = "Koblenz"
        var units = "metric"
        var lang = "de"
    }
}


