package com.example.bmweather.openweathermap

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.example.bmweather.utility.Load
import com.example.bmweather.MainActivity
import com.example.bmweather.R
import com.example.bmweather.SecondActivity
import com.example.bmweather.network.RetrofitBuilder
import com.example.bmweather.network.WeatherService
import com.example.bmweather.openweathermap.response.WeatherReport
import com.example.bmweather.utility.Utility
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import retrofit2.Callback
import retrofit2.Response

var imageIsInvisible = false
val load: Load = Load()
val mainActivityInstance = MainActivity()
val myUtilities: Utility = Utility()

class FetchWeatherData {
    companion object FetchWeatherData {
        val TAG: String? =
            FetchWeatherData::class.java.simpleName
        private val apiRequest: WeatherService = RetrofitBuilder.getRetrofitInstance().create(
            WeatherService::class.java
        )

        fun getCurrentWeatherReport(
            app_id: String,
            lat: String,
            lon: String,
            lang: String,
            units: String,
            exclude: String,
            mainActivity: MainActivity,
            progressBar: View,
            debugTag: String
        ) {
            load.start(progressBar = progressBar)
            Log.i(debugTag, "fetching started")
            Log.d(TAG, "onResponse response:: $app_id  $lat $lon $lang $units $exclude")
            apiRequest.getCurrentWeatherData(lat, lon, units, lang, app_id, exclude)
                .enqueue(object : Callback<WeatherReport> {
                    override fun onResponse(
                        call: retrofit2.Call<WeatherReport>,
                        response: Response<WeatherReport>
                    ) {
                        Log.i(debugTag, "fetching Response is working")
                        //On successful response builde string as defined later on
                        if (response.code() == 200 && response.code() != 400) {
                            Log.i(debugTag, "responding")
                            val weatherReport = response.body()!!
                            mainActivity.current(weatherReport.current)
                            // mainActivity.realTemp(weatherReport.current)
                            mainActivity.daily(weatherReport.daily[0])
                            mainActivity.fetchHourlyWeather(weatherReport.hourly.take(24))
                            mainActivity.uiUtility()
                            imageIsInvisible = false
                            mainActivity.displayCheck(imageIsInvisible)
                            load.done(progressBar = progressBar)
                        } else {
                            if (response.code() == 400) {
                               val msg=  response.message()
                                Log.i(debugTag, "bad  ®esponse 400,$msg")
                                imageIsInvisible = true
                                myUtilities.clearAllTextViews(mainActivity.getTextViewList())
                                mainActivity.displayCheck(imageIsInvisible)

                                myUtilities.makeSnackbar(
                                    mainActivity.binding.relativeLayout,
                                    R.string.invalid_city_name,
                                    LENGTH_INDEFINITE
                                )


                                load.done(progressBar = progressBar)
                            }

                            if (response.code() == 401) {
                                Log.i(debugTag, "bad  ®esponse ,401")
                                imageIsInvisible = true
                                myUtilities.clearAllTextViews(mainActivity.getTextViewList())
                                mainActivity.displayCheck(imageIsInvisible)

                                myUtilities.makeSnackbar(
                                    mainActivity.binding.relativeLayout,
                                    R.string.white_screen_msg,
                                    LENGTH_INDEFINITE
                                )


                                load.done(progressBar = progressBar)
                            }
                            if (response.code() == 404) {
                                Log.i(debugTag, "bad  ®esponse ERROR404")
                                imageIsInvisible = true
                                myUtilities.clearAllTextViews(mainActivity.getTextViewList())
                                mainActivity.displayCheck(imageIsInvisible)
                                load.done(progressBar = progressBar)

                                myUtilities.makeSnackbar(
                                    mainActivity.binding.relativeLayout,
                                    R.string.white_screen_msg,
                                    LENGTH_INDEFINITE
                                )


                                //    mainActivityInstance.displayCheck()
                                /*  mainActivity.cityName = mainActivity.lastCityCache
                                  mainActivity.sorryDisplayView()
                                  Toast.makeText(
                                      mainActivity.applicationContext, "City NoT Found.",
                                      Toast.LENGTH_SHORT
                                  ).show()*/
                            }
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

        fun getForeCastWeatherReport(
            app_id: String,
            lat: String,
            lon: String,
            lang: String,
            units: String,
            exclude: String,
            activity: SecondActivity
        ) {
            Log.d(TAG, "onResponse response:: $app_id  $lat $lon $lang $units $exclude")
            Log.i("SecAct", "request sent")
            apiRequest.getCurrentWeatherData(lat, lon, units, lang, app_id, exclude)
                .enqueue(object : Callback<WeatherReport> {
                    override fun onResponse(
                        call: retrofit2.Call<WeatherReport>,
                        response: Response<WeatherReport>
                    ) {
                        Log.i("SecAct", "responding")
                        //On successful response builde string as defined later on
                        if (response.code() == 200 && response.code() != 400) {

                            val weatherReport = response.body()!!
                            activity.fetchDailyWeather(weatherReport.daily.takeLast(7))
                            activity.uiUtility()
                        } else {
                            imageIsInvisible = true
                            myUtilities.clearAllTextViews(mainActivityInstance.getTextViewList())
                            mainActivityInstance.displayCheck(imageIsInvisible)

                            if (response.code() == 404) {
                                Log.i("SecAct", "404")
                                /*mainActivity.cityName = mainActivity.lastCityCache
                                mainActivity.sorryDisplayView()*/
                                Toast.makeText(
                                    activity.applicationContext, "City NoT Found.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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