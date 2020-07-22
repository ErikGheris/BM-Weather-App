package com.example.bmweather

import android.util.Log
import com.example.bmweather.Network.WeatherService
import com.example.bmweather.ResponseModel.current.WeatherReport
import retrofit2.Callback
import retrofit2.Response
import source.open.akash.mvvmlogin.Model.nextdayforecast.WeatherNextDaysReport
import source.open.akash.mvvmlogin.Network.RetrofitRequest

class FetchWeatherData {
    companion object FetchWeatherData {
        val TAG: String?=FetchWeatherData::class.java.simpleName
        private val apiRequest: WeatherService  = RetrofitRequest.getRetrofitInstance().create(WeatherService::class.java)


        fun getCurrentWeatherReport(app_id: String,q: String,lang: String,units: String, mainActivity: MainActivity) {


            Log.d(TAG, "onResponse response:: $app_id  $q $lang $units")
            apiRequest.getCurrentWeatherData(q, units, lang, app_id)
                .enqueue(object : Callback<WeatherReport> {
                    override fun onResponse(
                        call: retrofit2.Call<WeatherReport>,
                        response: Response<WeatherReport>
                    ) {
                        //On successful response builde string as defined later on
                        if (response.code() == 200) {
                            val weatherReport = response.body()!!
                            mainActivity.temp(weatherReport.main)
                            mainActivity.tempallday(weatherReport.main)
                            mainActivity.realTemp(weatherReport.main)
                            mainActivity.city(weatherReport)
                            mainActivity.weather(weatherReport.weather[0])
                        }
                        else
                            if (response.code()==404){

                                /* finish();
                                 overridePendingTransition(0, 0);
                                 startActivity(intent);
                                 overridePendingTransition(0, 0);
                                 Toast.makeText(
                                     applicationContext, "City NoT FounD",
                                     Toast.LENGTH_SHORT
                                 ).show() */

                                /*
                                     finish();
                                     startActivity(intent);*/

                            }
                    }
                    //Message in the case of failed API call
                    override fun onFailure(call: retrofit2.Call<WeatherReport>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        }
        fun getForecastWeatherReport(app_id: String,q: String,lang: String,units: String,cnt: String, mainActivity: MainActivity) {
            Log.d(TAG, "onResponse response:: $app_id  $q $lang $units $cnt")
            apiRequest.getForecastWeatherData(q, units,cnt, lang, app_id)
                .enqueue(object : Callback<WeatherNextDaysReport> {
                    override fun onResponse(
                        call: retrofit2.Call<WeatherNextDaysReport>,
                        response: Response<WeatherNextDaysReport>
                    ) {
                        //On successful response builde string as defined later on
                        if (response.code() == 200) {
                            val weatherNextDaysReport = response.body()!!
                            mainActivity.forecast(weatherNextDaysReport.list)
                        }
                        else
                            if (response.code()==404){

                                /* finish();
                                 overridePendingTransition(0, 0);
                                 startActivity(intent);
                                 overridePendingTransition(0, 0);
                                 Toast.makeText(
                                     applicationContext, "City NoT FounD",
                                     Toast.LENGTH_SHORT
                                 ).show() */

                                /*
                                     finish();
                                     startActivity(intent);*/

                            }
                    }
                    override fun onFailure(call: retrofit2.Call<WeatherNextDaysReport>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        }
    }
}