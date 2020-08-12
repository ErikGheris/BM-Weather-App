package com.example.bmweather

import android.util.Log
import android.widget.Toast
import com.example.bmweather.Network.WeatherService
import com.example.bmweather.response.WeatherReport
import retrofit2.Callback
import retrofit2.Response
import source.open.akash.mvvmlogin.Network.RetrofitRequest

class FetchWeatherData {
    companion object FetchWeatherData {
        val TAG: String?=FetchWeatherData::class.java.simpleName
        private val apiRequest: WeatherService = RetrofitRequest.getRetrofitInstance().create(
            WeatherService::class.java)

        fun getCurrentWeatherReport(app_id: String,lat: String, lon: String,lang: String,units: String,exclude: String, mainActivity: MainActivity) {


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
            mainActivity.delayHandler()
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
                            activity.fetchDailyWeather(weatherReport.daily)
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