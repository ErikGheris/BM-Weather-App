package com.example.bmweather


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
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
    val TAG = "thisTAG"
    var resultMessage = "SKSKSK"
    lateinit var mLastLocation: Location
    override var xCoordination: String = ""
    override var yCoordination: String = ""
    override var countryCode: String = ""
    override var locality: String = ""
    private val apiKey = "6133b390a077c487bc9ac43311b3ba26"
    var cityName = ""
    private var units = "metric"
    private var lang = "de"
    var lastCityCache = cityName
    private var searched: String = ""
    private var exclude = "hourly,minutely"
    private val fetchWeather = FetchWeatherData
    var searchedxCoordination = ""
    var searchedyCoordination = ""
    lateinit var lastLocation: LastLocation
    lateinit var binding: ActivityMainBinding
    var searching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainActivityContext = applicationContext
        lastLocation = LastLocation(mainActivityContext)

        lastLocation.setupPermissions(this, this)
        //!!!!!!!!!!!!!!
        lastLocation.setUpLocationListener(
            this, this
        )

        searchButtonAction()

        binding.searchInput.setOnClickListener {
            clearInputText(binding.searchInput)
        }

        swipeAction()

        activityButtonAction()

    }


    override fun onStart() {
        super.onStart()
        val mProgressBar = findViewById<ProgressBar>(R.id.Progress)
        if (xCoordination.isEmpty()) {
            Handler().postDelayed({
                binding.city.text = getString(R.string.City, locality, countryCode)
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
                    this, "Data Updated, Coordinates are $xCoordination, $yCoordination",
                    Toast.LENGTH_SHORT
                ).show()
                // Hide swipe to refresh icon animation
                swipe.isRefreshing = false

                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                mProgressBar.visibility = View.GONE;
            }, 4000)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            );
        } else {
            if (!searching) {
                    binding.city.text = getString(R.string.City, locality, countryCode)
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
                        this, "Data Updated, Coordinates are $xCoordination, $yCoordination",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Hide swipe to refresh icon animation
                    swipe.isRefreshing = false

                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    mProgressBar.visibility = View.GONE;

            } else {
                lastCityCache = cityName
                cityName = searched
                searchedxCoordination = lastLocation.toLatitude(cityName)
                searchedyCoordination = lastLocation.toLongitude(cityName)
                setSearchedCityInfoInTV()
                fetchWeather.getCurrentWeatherReport(
                    apiKey,
                    lat = searchedxCoordination,
                    lon = searchedyCoordination,
                    lang = lang,
                    units = units,
                    exclude = exclude,
                    mainActivity = this
                )
                mProgressBar.visibility = View.GONE;
            }
        }
    }


    private fun searchButtonAction() {
        binding.searchButton.setOnClickListener {
            searched = Search().get(search_input).toString()
            searching = true
            if (searched.trim().isNotEmpty()) {
                lastCityCache = cityName
                cityName = searched
                searchedxCoordination = lastLocation.toLatitude(cityName)
                searchedyCoordination = lastLocation.toLongitude(cityName)
                setSearchedCityInfoInTV()
                fetchWeather.getCurrentWeatherReport(
                    apiKey,
                    lat = searchedxCoordination,
                    lon = searchedyCoordination,
                    lang = lang,
                    units = units,
                    exclude = exclude,
                    mainActivity = this
                )
                //safe city
                Toast.makeText(
                    this,
                    "looking for $searched's Weather Info, Coordinates are $searchedxCoordination $searchedyCoordination",
                    Toast.LENGTH_SHORT
                )
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
    }


        private fun activityButtonAction() {
            binding.activityButton.setOnClickListener() {
                val intent = Intent(this, SecondActivity::class.java)
                if (!searching) {
                intent.putExtra("xCoordination", xCoordination);
                intent.putExtra("yCoordination", yCoordination); } else {
                    intent.putExtra("xCoordination", searchedxCoordination);
                    intent.putExtra("yCoordination", searchedyCoordination);
                }
                Toast.makeText(
                    this,
                    "Forcast for:  $searchedxCoordination, $searchedyCoordination",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intent)

            }
        }

    private fun swipeAction() {

        binding.swipe.setOnRefreshListener {
            searching = false
            binding.city.text = getString(R.string.City, locality, countryCode)
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
            Load().done(progress_widget)
        }, 1000) // 1000 milliseconds

    }

    val sunformat = SimpleDateFormat("kk:mm")

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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            com.example.bmweather.Location.Location().permissionsList_request_Code -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    lastLocation.setUpLocationListener(
                        this, this
                    )

                    // getCurrentLocationName()


                    Log.i(
                        com.example.bmweather.Location.Location().TAG,
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


    private fun networkCheck(context: Context): Boolean {
        val statusCheck = networkAvailabilityStatus(context)
        return if (!statusCheck) {
            Toast.makeText(context, "Internet is not Available", Toast.LENGTH_SHORT).show()
            false
        } else
            true
    }


    private fun networkAvailabilityStatus(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result
    }


}
