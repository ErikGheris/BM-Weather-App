package com.example.bmweather


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bmweather.Location.LastLocation
import com.example.bmweather.databinding.ActivityMainBinding
import com.example.bmweather.response.Current
import com.example.bmweather.response.Daily
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), LocationReceiver {
    override var xCoordination: String = ""
    override var yCoordination: String = ""
    private val apiKey = "6133b390a077c487bc9ac43311b3ba26"
    var cityName = ""
    private var units = "metric"
    private var lang = "de"
    var lastCityCache = cityName
    private var searched: String = ""
    private var longitude: String = ""
    private var latitude: String = ""
    private var exclude = "hourly,minutely"
    private val fetchWeather = FetchWeatherData

    lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()

        longitude = yCoordination
        latitude = xCoordination
        //viewBinding initialization and assignment
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LastLocation().setupPermissions(this, this)
        LastLocation().setUpLocationListener(
            binding.latTextView,
            binding.lngTextView,
            this,
            this
        )


        //toaster Message + get current data
        binding.searchButton.setOnClickListener {
            searched = Search().get(search_input).toString()
            if (searched.trim().isNotEmpty()) {
                lastCityCache = cityName
                cityName = searched
                fetchWeather.getCurrentWeatherReport(
                    apiKey,
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
        binding.searchInput.setOnClickListener {
            clearInputText(binding.searchInput)
        }
        binding.swipe.setOnRefreshListener {
            fetchWeather.getCurrentWeatherReport(
                app_id = apiKey,
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
        Handler().postDelayed({
            fetchWeather.getCurrentWeatherReport(
                app_id = apiKey,
                lat = xCoordination,
                lon = yCoordination,
                lang = lang,
                units = units,
                exclude = exclude,
                mainActivity = this
            )
        }, 3000)

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

    fun delayHandler() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            //wait x delay MS and then progress is done
            Load().done(progress_widget)
        }, 1000) // 1000 milliseconds

    }
    val sunformat = SimpleDateFormat("kk:mm" )

    fun current(main: Current) {
        binding.mainTemp.text = main.temp.roundToInt().toString().plus(getString(R.string.temp_unit_c))
        binding.sunrise.text = sunformat.format(main.sunrise * 1000L).toString()
        binding.sunset.text = sunformat.format(main.sunset * 1000L).toString()
        binding.feelslike.text = main.feelsLike.roundToInt().toString().plus(getString(R.string.temp_unit_c))
        binding.wind.text = main.windSpeed.roundToInt().toString().plus(getString(R.string.kmh))
        binding.description.text = main.weather[0].description
        Picasso.get()
            .load("http://openweathermap.org/img/wn/" + main.weather[0].icon + "@2x.png")
            .resize(250, 250)
            .into(ic_description)
    }

    fun daily(weather: Daily) {
        binding.mintemp.text = weather.temp.min.roundToInt().toString().plus(getString(R.string.temp_unit_c))
        binding.maxtemp.text = weather.temp.max.roundToInt().toString().plus(getString(R.string.temp_unit_c))
    }

    /*
        fun city(WeatherReport: WeatherReport) {
        city.text = WeatherReport.name.plus(getString(R.string.comma)).plus(WeatherReport.sys.country)
    }

    fun forecast(main: List<ListData>) {
        forecast.text = getString(R.string.weather_des).plus(main[0])
    } */




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
