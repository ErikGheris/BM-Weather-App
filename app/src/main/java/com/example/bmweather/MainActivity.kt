package com.example.bmweather


import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bmweather.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Runnable
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    val boolean: Boolean = true
    var fusedLocationClient: FusedLocationProviderClient? = null

    // Declare parameters for tge GET funktion
    private val BaseUrl = "http://api.openweathermap.org/"
    private val APIKey = "6133b390a077c487bc9ac43311b3ba26"
    private var cityName = "Berlin"
    var units = "metric"
    var lang = "de"
    var lastCityCache = cityName
    var searched: String = ""

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewBinding initialization and assignment
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        Location().setupPermissions(this, this)


        //
        binding.locationButton.setOnClickListener {
            // Location().setupPermissions(this, this)
            // Location().isLocationEnabled(this)
            Location().setUpLocationListener(
                binding.latTextView,
                binding.lngTextView,
                this,
                this
            )
        }

        //toaster Message + get current data
        binding.searchButton.setOnClickListener {
            searched = Search().get(search_input).toString()
            if (searched.trim().isNotEmpty()) {
                lastCityCache = cityName
                cityName = searched
                getCurrentData()
                //safe city
                Toast.makeText(this, "looking for $searched's Weather Info", Toast.LENGTH_SHORT)
                    .show()
                clearInputText(binding.searchInput)

            } else {
                Toast.makeText(
                    this, "Please enter a Location!",
                    Toast.LENGTH_SHORT
                ).show()
                clearInputText(binding.searchInput)
            }
        }

        // clears the autoCompleteTExtView when it is clicked
        binding.searchInput.setOnClickListener {
            clearInputText(binding.searchInput)
        }

        // all about pull to refresh data
        binding.swipe.setOnRefreshListener {
            getCurrentData()
            Toast.makeText(
                this, "Data Updated",
                Toast.LENGTH_SHORT
            ).show()
            // Hide swipe to refresh icon animation
            swipe.isRefreshing = false
        }


    }

    private fun clearInputText(textView: AutoCompleteTextView) {
        textView.setText("")
    }

    //Retrofit based API request
    private fun getCurrentData() {
        // progress starts
        Load().start(progress_widget)
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
            app_id = APIKey
        )
        //val call = service.getCurrentWeatherData(lat, lon, AppId)
        //Expected response
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: retrofit2.Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                //On successful response builde string as defined later on
                if (response.code() == 200 && response.code() != 400) {
                    cityName = searched
                    correctResponse(response)
                } else
                    if (response.code() == 404) {
                        cityName = lastCityCache
                        sorryDisplayView()
                        Toast.makeText(
                            applicationContext, "City NoT Found.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            private fun correctResponse(response: Response<WeatherResponse>) {
                val weatherResponse = response.body()!!
                //Build String from response
                val stringBuilder = null
                binding.description.text = stringBuilder
                rain(weatherResponse.weather[0])
                temp(weatherResponse.main!!)
                tempallday(weatherResponse.main!!)
                realTemp(weatherResponse.main!!)
                city(weatherResponse)
            }

            //Message in the case of failed API call
            override fun onFailure(call: retrofit2.Call<WeatherResponse>, t: Throwable) {
                binding.description.text = t.message
            }
        })
        //delay to show the progress !!! JUST TO SHOW IT WORKS!!
        delayHandler()
    }

    private fun delayHandler() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            //wait x delay MS and then progress is done
            Load().done(progress_widget)
        }, 1000) // 1000 milliseconds

    }

    // Display API response in specific textview
    private fun rain(weather: Weather) {
        binding.description.text = getString(R.string.weather_des).plus(weather.description)
    }


    private fun temp(main: Main) {
        mainTemp.text = "".plus(main.temp.toUInt()).plus(getString(R.string.temp_unit_c))
    }

    private fun tempallday(main: Main) {
        binding.TempAllDay.text =
            getString(R.string.min_temp).plus(main.temp_min.toUInt()).plus("  ").plus(
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

    private fun sorryDisplayView() {
        city.text = getString(R.string.sorry)
        realTemp.text = getString(R.string.empty)
        binding.TempAllDay.text = getString(R.string.empty)
        binding.description.text = getString(R.string.empty)
        mainTemp.text = getString(R.string.empty)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Location().permissionsList_request_Code -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Location().setUpLocationListener(
                        binding.latTextView, binding.lngTextView,
                        this, this
                    )
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
