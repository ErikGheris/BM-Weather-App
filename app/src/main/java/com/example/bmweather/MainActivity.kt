package com.example.bmweather


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bmweather.Fragments.CurrentWeather
import com.example.bmweather.Fragments.Forecast
import com.example.bmweather.Location.LastLocation
import com.example.bmweather.databinding.ActivityMainBinding
import com.example.bmweather.response.Current
import com.example.bmweather.response.Daily
import com.example.bmweather.response.Weather
import com.google.android.gms.location.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.fragment_forecast.*
import kotlinx.coroutines.Runnable


class MainActivity : AppCompatActivity(), LocationReceiver {
    val boolean: Boolean = true
    var fusedLocationClient: FusedLocationProviderClient? = null
    override var xCoordination: String = ""
    override var yCoordination: String = ""

    // Declare parameters for tge GET funktion
    val app_id = "6133b390a077c487bc9ac43311b3ba26"
    var cityName = "Berlin"
    var units = "metric"
    var lang = "de"
    var lastCityCache = cityName
    var searched: String = ""
    var longitude: String = ""
    var latitude: String = ""
    var exclude = "hourly,minutely"


    private val fetchWeather = FetchWeatherData

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewBinding initialization and assignment
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        LastLocation().setUpLocationListener(
            binding.latTextView,
            binding.lngTextView,
            this,
            this
        )

        //
        LastLocation().setupPermissions(this, this)


        /*
       like this we can Access to our coordination, they can be used later*/
        //


        binding.locationButton.setOnClickListener {
            // Location().setupPermissions(this, this)
            // Location().isLocationEnabled(this)

            Toast.makeText(this, "hi  $xCoordination  ,     $yCoordination", Toast.LENGTH_SHORT)
                .show()

        }


        /*button_2.setOnClickListener {

             fetchWeather.getForeCastWeatherReport(app_id,lat=Search().get(latTextView), lon = Search().get(lngTextView), lang = lang,units = units,exclude = exclude, mainActivity = this)

          }*/

        //toaster Message + get current data
        binding.searchButton.setOnClickListener {
            searched = Search().get(search_input).toString()
            if (searched.trim().isNotEmpty()) {
                lastCityCache = cityName
                cityName = searched
                fetchWeather.getCurrentWeatherReport(
                    app_id,
                    lat = xCoordination,
                    lon = yCoordination,
                    lang = lang,
                    units = units,
                    exclude = exclude,
                    mainActivity = this
                )
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
            fetchWeather.getCurrentWeatherReport(
                app_id,
                lat = xCoordination,
                lon = yCoordination,
                lang = lang,
                units = units,
                exclude = exclude,
                mainActivity = this
            )
            Toast.makeText(
                this, "Data Updated",
                Toast.LENGTH_SHORT
            ).show()
            // Hide swipe to refresh icon animation
            swipe.isRefreshing = false
        }

        binding.fragment.setOnClickListener() {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("xCoordination", xCoordination);
            intent.putExtra("yCoordination", yCoordination);
            Toast.makeText(this, "$xCoordination    $yCoordination", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        /*     var state = 0

             val forecastFragment = Forecast()
             val currentWeatherFragment = CurrentWeather()
             binding.fragment.setOnClickListener {
                 when (state) {

                     0 -> {
                         supportFragmentManager.beginTransaction().apply {
                             state = 1
                             replace(R.id.frameLayout, forecastFragment)
                             commit()
                         }
                     }

                     1 -> {
                         supportFragmentManager.beginTransaction().apply {
                             state = 0
                             replace(R.id.frameLayout, currentWeatherFragment)
                             commit()
                         }
                     }


                     else -> state=0
                 }
             }*/
    }


    /*  fun sendMessage(view: View) {
          val editText = findViewById<EditText>(R.id.editText)
          val message = editText.text.toString()
          val intent = Intent(this, DisplayMessageActivity::class.java).apply {
              putExtra(EXTRA_MESSAGE, message)
          }
          startActivity(intent)
      }
  */


    private fun clearInputText(textView: AutoCompleteTextView) {
        textView.setText("")
    }

    fun getTextViewValue(latTextView: TextView): String? {
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
    fun temp(main: Current) {
        mainTemp.text = "".plus(main.temp.toUInt()).plus(getString(R.string.temp_unit_c))
    }

    fun tempallday(weather: Daily) {
        binding.TempAllDay.text =
            getString(R.string.min_temp).plus(weather.temp.min.toUInt()).plus("  ")
                .plus(getString(R.string.max_temp)).plus(weather.temp.max.toUInt())
    }

    fun ic_description(weather: List<Weather>) {
        Picasso.get()
            .load("http://openweathermap.org/img/wn/" + weather?.get(0)?.icon + "@2x.png")
            .into(ic_description)
    }

    fun realTemp(main: Current) {
        realTemp.text = getString(R.string.feels_like_temp).plus(main.feelsLike.toUInt())
    }


    fun weather(weather: Weather) {
        binding.description.text = getString(R.string.weather_des).plus(weather.description)
    }


    /*
        fun city(WeatherReport: WeatherReport) {
        city.text = WeatherReport.name.plus(getString(R.string.comma)).plus(WeatherReport.sys.country)
    }







    fun forecast(main: List<ListData>) {
        forecast.text = getString(R.string.weather_des).plus(main[0])
    } */


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
                    LastLocation().setUpLocationListener(
                        binding.latTextView, binding.lngTextView,
                        this, this
                    )
                    Log.i(
                        com.example.bmweather.Location.Location().TAG,
                        "Permission has been denied by user"
                    )
                    Toast.makeText(
                        this, "Permission has been denied by user",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.i(
                        com.example.bmweather.Location.Location().TAG,
                        "Permission has been granted by user"
                    )
                    Toast.makeText(
                        this, "Permission has been granted by user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}
