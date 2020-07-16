package com.example.bmweather


import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Runnable
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
class MainActivity : AppCompatActivity() {

    //Declare var for Textview with late init
    private lateinit var weatherDescription: TextView
    private lateinit var mainTemp: TextView
    private lateinit var tempAllDay: TextView
    private lateinit var city: TextView
    private lateinit var realTemp: TextView


    // Declare parameters for tge GET funktion


    private val BaseUrl = "http://api.openweathermap.org/"
    private val APIKey = "6133b390a077c487bc9ac43311b3ba26"
    var cityName = "Berlin"
    var units = "metric"
    var lang = "de"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    Location().setupPermissions(this, this)


        location_button.setOnClickListener {

                 Toast.makeText(
                      this, "IT WORKS",
                      Toast.LENGTH_SHORT
                  ).show()
        }


        //toaster Message + get current data
        search_button.setOnClickListener {
            val searched = search().get(search_input)
            cityName = searched.toString()
            Toast.makeText(this, "looking for $searched's Weather Info", Toast.LENGTH_SHORT).show()
            getCurrentData()
        }

        // clears the autoCompleteTExtView when it is clicked
        search_input.setOnClickListener {
            search_input.setText("")
        }

        // all about pull to refresh data
        swipe.setOnRefreshListener {

            getCurrentData()
            Toast.makeText(
                this, "Data Updated",
                Toast.LENGTH_SHORT
            ).show()

            // Hide swipe to refresh icon animation
            swipe.isRefreshing = false
        }


        //Get textview by id

        weatherDescription = findViewById(R.id.description)

        mainTemp = findViewById(R.id.mainTemp)

        tempAllDay = findViewById(R.id.TempAllDay)

        city = findViewById(R.id.city)

        realTemp = findViewById(R.id.realTemp)

    }

 /*   fun getContext(): Context? {
        return sContext
    } */


    //Retrofit based API request
    private fun getCurrentData() {
        // progress starts
        progress().start(progress_widget)
   // Location().showToast(this,"PISSSSSSSS")

        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            //Generate an implementation for deserialization
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherService::class.java)
        //add parameters to BaseURL
        val call = service.getCurrentWeatherData(
            q = cityName,
            units = units,
            lang = lang,
            app_id = APIKey)
        //val call = service.getCurrentWeatherData(lat, lon, AppId)

        //Expected response
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: retrofit2.Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                //On successful response builde string as defined later on
                if (response.code() == 200) {
                    val weatherResponse = response.body()!!
                    //Build String from response
                    val stringBuilder = null
                    weatherDescription.text = stringBuilder
                    rain(weatherResponse.weather[0])
                    temp(weatherResponse.main!!)
                    tempallday(weatherResponse.main!!)
                    realTemp(weatherResponse.main!!)
                    city(weatherResponse)
                }
            }
            //Message in the case of failed API call
            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                weatherDescription.text = t.message

            }
        })

     /*   //assigning the input to the searched on click
        search_button.setOnClickListener {
            val searched = search().get(search_input)
            Toast.makeText(this, "looking for $searched's Weather Info", Toast.LENGTH_SHORT).show()
        }
*/

        //delay to show the progress !!! JUST TO SHOW IT WORKS!!
        val handler = Handler()
        handler.postDelayed(Runnable {
            //wait x delay MS and then progress is done
            progress().done(progress_widget)
        }, 1000) // 1000 milliseconds

    }


    // Display API response in specific textview


    private fun rain(weather: Weather) {
        weatherDescription.text = getString(R.string.weather_des).plus(weather.description)
    }

    private fun temp(main: Main) {
        mainTemp.text = "".plus(main.temp.toUInt()).plus(getString(R.string.temp_unit_c))
    }

    private fun tempallday(main: Main) {
        tempAllDay.text = getString(R.string.min_temp).plus(main.temp_min.toUInt()).plus("  ").plus(
            getString(
                R.string.max_temp
            )
        ).plus(main.temp_max.toUInt())
    }

    private fun realTemp(main: Main) {
        realTemp.text = getString(R.string.feels_like_temp).plus(main.feels_like.toUInt())
    }

    private fun city(weatherResponse: WeatherResponse) {
        city.text =
            weatherResponse.name.plus(getString(R.string.comma)).plus(weatherResponse.sys!!.country)
    }


    // Declare parameters for tge GET funktion




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Location().permissionsList_request_Code -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(Location().TAG, "Permission has been denied by user")
                      Toast.makeText(
                                      this, "Permission has been denied by user",
                                      Toast.LENGTH_SHORT
                                  ).show()

                } else {
                    Log.i(Location().TAG, "Permission has been granted by user")
                      Toast.makeText(
                                      this, "Permission has been granted by user",
                                      Toast.LENGTH_SHORT
                                  ).show()
                }
            }
        }


    }
}
