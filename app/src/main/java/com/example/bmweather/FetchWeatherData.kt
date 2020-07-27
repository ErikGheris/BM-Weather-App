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
        private val apiRequest: WeatherService  = RetrofitRequest.getRetrofitInstance().create(WeatherService::class.java)

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
                            mainActivity.temp(weatherReport.current)
                            mainActivity.realTemp(weatherReport.current)
                            mainActivity.tempallday(weatherReport.daily[0])
                            mainActivity.ic_description(weatherReport.current.weather)
                            mainActivity.weather(weatherReport.current.weather[0])

                        }
                        else
                            if (response.code()==404){

                                mainActivity.cityName = mainActivity.lastCityCache
                                mainActivity.sorryDisplayView()
                                Toast.makeText(
                                    mainActivity.applicationContext, "City NoT Found.",
                                    Toast.LENGTH_SHORT
                                ).show()

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
                            activity.temp1(weatherReport.daily[1])
                            activity.temp2(weatherReport.daily[2])
                            activity.temp3(weatherReport.daily[3])
                            activity.temp4(weatherReport.daily[4])
                            activity.temp5(weatherReport.daily[5])
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