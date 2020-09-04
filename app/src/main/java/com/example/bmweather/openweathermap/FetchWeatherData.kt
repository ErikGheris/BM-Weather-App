package com.example.bmweather.openweathermap

import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.bmweather.utility.Load
import com.example.bmweather.MainActivity
import com.example.bmweather.SecondActivity
import com.example.bmweather.network.RetrofitBuilder
import com.example.bmweather.network.WeatherService
import com.example.bmweather.openweathermap.response.WeatherReport
import retrofit2.Callback
import retrofit2.Response
val load: Load =
    Load()

class FetchWeatherData {
    companion object FetchWeatherData {
        val TAG: String?=
            FetchWeatherData::class.java.simpleName
        private val apiRequest: WeatherService = RetrofitBuilder.getRetrofitInstance().create(
            WeatherService::class.java)
        fun getCurrentWeatherReport(app_id: String, lat: String, lon: String, lang: String, units: String, exclude: String, mainActivity: MainActivity, progressBar: View) {
                load.start(progressBar = progressBar)
            Log.i("THISISBS","fetching started")

            Log.d(TAG, "onResponse response:: $app_id  $lat $lon $lang $units $exclude")
            apiRequest.getCurrentWeatherData(lat, lon, units, lang, app_id, exclude)
                .enqueue(object : Callback<WeatherReport> {
                    override fun onResponse(
                        call: retrofit2.Call<WeatherReport>,
                        response: Response<WeatherReport>
                    ) {
                        //On successful response builde string as defined later on
                        if (response.code() == 200 && response.code() != 400) {
                            val weatherReport = response.body()!!
                            mainActivity.current(weatherReport.current)
                           // mainActivity.realTemp(weatherReport.current)
                            mainActivity.daily(weatherReport.daily[0])
                            mainActivity.fetchHourlyWeather(weatherReport.hourly.take(24))
                            mainActivity.uiUtility()
                            load.done(progressBar = progressBar)
                        }
                        else
                            if (response.code()==404){

                              /*  mainActivity.cityName = mainActivity.lastCityCache
                                mainActivity.sorryDisplayView()
                                Toast.makeText(
                                    mainActivity.applicationContext, "City NoT Found.",
                                    Toast.LENGTH_SHORT
                                ).show()*/

                            }
                    }
                    //Message in the case of failed API call
                    override fun onFailure(call: retrofit2.Call<WeatherReport>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            // TODO: 19.08.20 commented because it was causing a problem
         //   mainActivity.delayHandler()
        }
        fun getForeCastWeatherReport(app_id: String, lat: String, lon: String, lang: String, units: String, exclude: String, activity: SecondActivity) {
            Log.d(TAG, "onResponse response:: $app_id  $lat $lon $lang $units $exclude")
            apiRequest.getCurrentWeatherData(lat, lon, units, lang, app_id, exclude)
                .enqueue(object : Callback<WeatherReport> {
                    override fun onResponse(
                        call: retrofit2.Call<WeatherReport>,
                        response: Response<WeatherReport>
                    ) {
                        //On successful response builde string as defined later on
                        if (response.code() == 200 && response.code() != 400) {
                            val weatherReport = response.body()!!
                            activity.fetchDailyWeather(weatherReport.daily.takeLast(7))
                            activity.uiUtility()
                        }
                        else
                            if (response.code()==404){

                                /*mainActivity.cityName = mainActivity.lastCityCache
                                mainActivity.sorryDisplayView()*/
                                Toast.makeText(
                                    activity.applicationContext, "City NoT Found.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                    }
                    //Message in the case of failed API call
                    override fun onFailure(call: retrofit2.Call<WeatherReport>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
          //  mainActivity.delayHandler()
        }
    }

}