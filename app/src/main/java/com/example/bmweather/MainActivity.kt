package com.example.bmweather

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmweather.adapter.HourlyArrayAdapter
import com.example.bmweather.network.ConnectivityManagement
import com.example.bmweather.databinding.ActivityMainBinding
import com.example.bmweather.location.LastLocation
import com.example.bmweather.location.LocationReceiver
import com.example.bmweather.openweathermap.FetchWeatherData
import com.example.bmweather.openweathermap.response.Current
import com.example.bmweather.openweathermap.response.Daily
import com.example.bmweather.openweathermap.response.Hourly
import com.example.bmweather.utility.Load
import com.example.bmweather.utility.Search
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),
    LocationReceiver {
    override var xCoordination: String = ""
    override var yCoordination: String = ""
    override var countryCode: String = ""
    override var locality: String = ""
    private var searchedXCoordination = ""
    private var searchedYCoordination = ""
    private val apiKey = "6133b390a077c487bc9ac43311b3ba26"
    private var cityName = ""
    private var units = "metric"
    private var lang = "en"
    private var lastCityCache = cityName
    private var searched: String = ""
    private var exclude = "minutely"
    private val fetchWeather =
        FetchWeatherData
    private lateinit var lastLocation: LastLocation
    private lateinit var binding: ActivityMainBinding
    private var searching = false
    private var load: Load =
        Load()
    lateinit var connectivityManagement: ConnectivityManagement
    // TODO: 12.08.20   lazy declarataion vs inFunctionDeclaration
    //  val list : ArrayList by lazy { ArrayList() }
    // private lateinit var backToast: Toast

    private val backToast: Toast by lazy {
        Toast.makeText(
            this,
            "Press back again to leave the app.",
            Toast.LENGTH_SHORT
        )
    }
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainActivityContext = applicationContext
        lastLocation = LastLocation(mainActivityContext)
        connectivityManagement = ConnectivityManagement(mainActivityContext)

        lastLocation.setupPermissions(this, this)

        lastLocation.setUpLocationListener(
            this, this
        )

        binding.searchInput.setOnClickListener {
            clearInputText(binding.searchInput)
        }

        searchButtonAction()
        swipeAction()
        activityButtonAction()

    }


    override fun onStart() {
        super.onStart()

        if (xCoordination.isEmpty()) {
            Handler().postDelayed({
                binding.city.text = getString(R.string.City, locality, countryCode)
                makeCurrentLocationWeatherRequest()
                Toast.makeText(
                    this, "Data Updated, Coordinates are $xCoordination, $yCoordination",
                    Toast.LENGTH_SHORT
                ).show()
                // Hide swipe to refresh icon animation
                swipe.isRefreshing = false
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                load.done(binding.Progress)
            }, 4000)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            if (!searching) {
                binding.city.text = getString(R.string.City, locality, countryCode)
                makeCurrentLocationWeatherRequest()
                Toast.makeText(
                    this, "Data Updated, Coordinates are $xCoordination, $yCoordination",
                    Toast.LENGTH_SHORT
                ).show()
                // Hide swipe to refresh icon animation
                swipe.isRefreshing = false

                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                load.done(binding.Progress)

            } else {
                lastCityCache = cityName
                cityName = searched
                setSearchedCoordinates()
                setSearchedCityInfoInTV()
                makeSearchWeatherRequest()
                load.done(binding.Progress)
            }
        }
    }

    override fun onBackPressed() {
        // backToast = Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_SHORT)
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            super.onBackPressed()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }


    override fun onStop() {
        super.onStop()


    }
    private fun searchButtonAction() {

        binding.searchButton.setOnClickListener {

            searched = Search().get(search_input).toString()
            searching = true
            if (searched.trim().isNotEmpty()) {
                lastCityCache = cityName
                cityName = searched
                if (connectivityManagement.networkCheck(this)) {
                    setSearchedCoordinates()
                    setSearchedCityInfoInTV()
                    makeSearchWeatherRequest()
                    //safe city
                    Toast.makeText(
                        this,
                        "looking for $searched's Weather Info, Coordinates are $searchedXCoordination $searchedYCoordination",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    clearInputText(binding.searchInput)
                }
            } else {
                Toast.makeText(
                    this, "Please enter a Location!",
                    Toast.LENGTH_SHORT
                ).show()
                clearInputText(binding.searchInput)
            }
        }
    }

    private fun setSearchedCoordinates() {
        searchedXCoordination = lastLocation.toLatitude(cityName)
        searchedYCoordination = lastLocation.toLongitude(cityName)
    }

    private fun makeSearchWeatherRequest() {
        fetchWeather.getCurrentWeatherReport(
            apiKey,
            lat = searchedXCoordination,
            lon = searchedYCoordination,
            lang = Locale.getDefault().language,
            units = units,
            exclude = exclude,
            mainActivity = this,
            progressBar = binding.Progress
        )
    }

    private fun makeCurrentLocationWeatherRequest() {
        fetchWeather.getCurrentWeatherReport(
            app_id = apiKey,
            lat = xCoordination,
            lon = yCoordination,
            lang = Locale.getDefault().language,
            units = units,
            exclude = exclude,
            mainActivity = this,
            progressBar = binding.Progress
        )
    }

    private fun activityButtonAction() {
        binding.activityButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            if (!searching) {
                intent.putExtra("xCoordination", xCoordination)
                intent.putExtra("yCoordination", yCoordination); } else {
                intent.putExtra("xCoordination", searchedXCoordination)
                intent.putExtra("yCoordination", searchedYCoordination)
            }
            Toast.makeText(
                this,
                " Forecast for:  $searchedXCoordination, $searchedYCoordination",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(intent)
        }
    }

    private fun swipeAction() {

        binding.swipe.setOnRefreshListener {
            searching = false
            binding.city.text = getString(R.string.City, locality, countryCode)
            makeCurrentLocationWeatherRequest()
            Toast.makeText(
                this, "Data Updated, Coordinates are $xCoordination, $yCoordination",
                Toast.LENGTH_SHORT
            ).show()
            // Hide swipe to refresh icon animation
            swipe.isRefreshing = false
        }
    }

    private fun setSearchedCityInfoInTV() {
        val (locale, countryCode) = getCityInfo()
        binding.city.text = getString(R.string.City, locale, countryCode)
    }

    private fun getCityInfo(): Pair<String, String> {
        val locale = lastLocation.getLocaleFromName(cityName)
        val countryCode = lastLocation.getCountryCodeFromName(cityName)
        return Pair(locale, countryCode)
    }

    private fun clearInputText(textView: AutoCompleteTextView) {
        textView.setText("")
    }

    fun delayHandler() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            //wait x delay MS and then progress is done
            load.done(binding.progressWidget)
        }, 1000) // 1000 milliseconds

    }

    @SuppressLint("SimpleDateFormat")
    private val sunformat = SimpleDateFormat("kk:mm")

    fun current(main: Current) {
        binding.mainTemp.text =
            main.temp.roundToInt().toString().plus(getString(R.string.temp_unit_c))
        binding.sunrise.text = sunformat.format(main.sunrise * 1000L).toString()
        binding.sunset.text = sunformat.format(main.sunset * 1000L).toString()
        binding.feelslike.text =
            main.feelsLike.roundToInt().toString().plus(getString(R.string.temp_unit_c))
        binding.wind.text = main.windSpeed.roundToInt().toString().plus(getString(R.string.kmh))
        binding.description.text = main.weather[0].description
        Picasso.get()
            .load("http://openweathermap.org/img/wn/" + main.weather[0].icon + "@2x.png")
            .resize(250, 250)
            .into(ic_description)
    }

    fun daily(weather: Daily) {
        binding.mintemp.text =
            weather.temp.min.roundToInt().toString().plus(getString(R.string.temp_unit_c))
        binding.maxtemp.text =
            weather.temp.max.roundToInt().toString().plus(getString(R.string.temp_unit_c))
    }

   fun fetchHourlyWeather(hourly: List<Hourly>) {
       hourlylist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
       hourlylist.adapter = HourlyArrayAdapter(hourly)
   }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            lastLocation.permissionsRequestCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    lastLocation.setUpLocationListener(
                        this, this
                    )
                    Log.i(
                        lastLocation.tag,
                        "Permission has been denied by user"
                    )
                    Toast.makeText(
                        this, "Permission has been denied by user",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    lastLocation.setUpLocationListener(
                        this, this
                    )
                    Log.i(
                lastLocation.tag,
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
