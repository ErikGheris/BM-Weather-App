package com.example.bmweather


import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bmweather.FetchingData.FetchWeatherDataLocation
import com.example.bmweather.Location.Location
import com.example.bmweather.ResponseModel.current.Weather
import com.example.bmweather.ResponseModel.current.WeatherReport
import com.example.bmweather.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Runnable
import source.open.akash.mvvmlogin.Model.nextdayforecast.ListData

class MainActivity : AppCompatActivity() {
    val boolean: Boolean = true
    var fusedLocationClient: FusedLocationProviderClient? = null

    // Declare parameters for tge GET funktion
    val BaseUrl = "http://api.openweathermap.org/"
    val app_id = "6133b390a077c487bc9ac43311b3ba26"
    var cityName = "Berlin"
    var units = "metric"
    var lang = "de"
    var lastCityCache = cityName
    var searched: String = ""
    var cnt = "3"
    var longitude:String = ""
     var latitude:String =""
        private val fetchWeather = FetchWeatherData
        private val fetchWeatherLocation = FetchWeatherDataLocation
        lateinit var binding: ActivityMainBinding
      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewBinding initialization and assignment
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



          com.example.bmweather.Location.LastLocation().setUpLocationListener(
              binding.latTextView,
              binding.lngTextView,
              this,
              this
          )

        //
        com.example.bmweather.Location.Location().setupPermissions(this, this)






       /*
      like this we can Access to our coordination, they can be used later*/
        //


        binding.locationButton.setOnClickListener {
            // Location().setupPermissions(this, this)
            // Location().isLocationEnabled(this)
            
           

             fetchWeatherLocation.getCurrentLocationWeatherReport(app_id,lat=Search().get(latTextView), lon = Search().get(lngTextView),lang = lang ,units = units, mainActivity = this)
        }

        //toaster Message + get current data
        binding.searchButton.setOnClickListener {
            searched = Search().get(search_input).toString()
            if (searched.trim().isNotEmpty()) {
                lastCityCache = cityName
                cityName = searched
                fetchWeather.getCurrentWeatherReport(app_id,cityName,lang,units, this)
                fetchWeather.getForecastWeatherReport(app_id,cityName,lang,units,cnt,this)
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
            fetchWeather.getCurrentWeatherReport(app_id,cityName,lang,units, this)
            fetchWeather.getForecastWeatherReport(app_id,cityName,lang,units,cnt,this)
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

    private fun getTextViewValue(latTextView: TextView): String? {
        return Search().get(lngTextView)
    }


     fun delayHandler() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            //wait x delay MS and then progress is done
            Load().done(progress_widget)
        }, 1000) // 1000 milliseconds

    }

    // Display API response in specific textview
    fun weather(weather: Weather) {
        binding.description.text = getString(R.string.weather_des).plus(weather.description)
    }

    fun temp(main: com.example.bmweather.ResponseModel.current.Main) {
        mainTemp.text = "".plus(main.temp.toUInt()).plus(getString(R.string.temp_unit_c))
    }

    fun tempallday(main: com.example.bmweather.ResponseModel.current.Main) {
        binding.TempAllDay.text = getString(R.string.min_temp).plus(main.tempMin.toUInt()).plus("  ").plus(getString(R.string.max_temp)).plus(main.tempMax!!.toUInt())
    }

    fun realTemp(main: com.example.bmweather.ResponseModel.current.Main) {
        realTemp.text = getString(R.string.feels_like_temp).plus(main.temp.toUInt())
    }

    fun city(WeatherReport: WeatherReport) {
        city.text = WeatherReport.name.plus(getString(R.string.comma)).plus(WeatherReport.sys.country)
    }

    fun forecast(main: List<ListData>) {
        forecast.text = getString(R.string.weather_des).plus(main[0])
    }


    fun sorryDisplayView() {
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
            com.example.bmweather.Location.Location().permissionsList_request_Code -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    com.example.bmweather.Location.LastLocation().setUpLocationListener(
                        binding.latTextView, binding.lngTextView,
                        this, this
                    )
                    Log.i(com.example.bmweather.Location.Location().TAG, "Permission has been denied by user")
                    Toast.makeText(
                        this, "Permission has been denied by user",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.i(com.example.bmweather.Location.Location().TAG, "Permission has been granted by user")
                    Toast.makeText(
                        this, "Permission has been granted by user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}
