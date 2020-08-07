package com.example.bmweather


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
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
import androidx.core.app.ActivityCompat
import com.example.bmweather.Location.LastLocation
import com.example.bmweather.databinding.ActivityMainBinding
import com.example.bmweather.response.Current
import com.example.bmweather.response.Daily
import com.example.bmweather.response.Weather
import com.google.android.gms.location.FusedLocationProviderClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Runnable
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), LocationReceiver {
    val TAG = "thisTAG"
    var resultMessage = "SKSKSK"
    val REQUEST_LOCATION_PERMISSION = 87
    lateinit var mLastLocation: Location
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    val cachedLatitude = "0"
    val cachedLongitude = "0"
    override var xCoordination: String = ""
    override var yCoordination: String = ""
    override var countryCode: String = ""
    override var locality: String =""
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       LastLocation().setupPermissions(this, this)
        //!!!!!!!!!!!!!!
      LastLocation().setUpLocationListener(

            this,this
        )

        binding.searchButton.setOnClickListener {
            searched = Search().get(search_input).toString()
            if (searched.trim().isNotEmpty()) {
                lastCityCache = cityName
                cityName = searched
                val locale = getLocaleFromName(cityName)
                val countryCode = getCountryCodeFromName(cityName)
                xCoordination = toLatitude(cityName, this)
                yCoordination = toLongitude(cityName, this)
                binding.city.text = getString(R.string.City, locale, countryCode)
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
                Toast.makeText(
                    this,
                    "looking for $searched's Weather Info, Coordinates are $xCoordination $yCoordination",
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

        binding.searchInput.setOnClickListener {
            clearInputText(binding.searchInput)
        }





        binding.swipe.setOnRefreshListener {

            binding.city.text = getString(R.string.City,locality,countryCode)

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




        binding.fragment.setOnClickListener() {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("xCoordination", xCoordination);
            intent.putExtra("yCoordination", yCoordination);
            Toast.makeText(this, "Forcast for:  $xCoordination , $yCoordination", Toast.LENGTH_SHORT).show()
            startActivity(intent)

        }


        Handler().postDelayed({
            binding.city.text = getString(R.string.City,locality,countryCode)

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
            val mProgressBar = findViewById<ProgressBar>(R.id.Progress)
            mProgressBar.visibility = View.GONE;
        }, 4000)
       window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            com.example.bmweather.Location.Location().permissionsList_request_Code -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                   LastLocation().setUpLocationListener(
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
                    LastLocation().setUpLocationListener(
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




    fun getCountryCodeFromName(locationName: String): String {
        val addressListFromName: ArrayList<Address>
        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())
        addressListFromName = geocoder.getFromLocationName(locationName, 1) as ArrayList<Address>

        val countryCode = addressListFromName.get(0).countryCode
        return countryCode
    }

    fun getLocaleFromName(locationName: String): String {
        var locale: String = "LOCALE_DEFAULT"
        val addressList: ArrayList<Address>
        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())
        try {
            addressList =
                geocoder.getFromLocationName(locationName, 1) as ArrayList<Address>
            locale = addressList.get(0).locality

        } catch (e: IOException) {
            resultMessage = this.getString(R.string.service_not_available)
            Log.e(TAG, resultMessage, e)
        }

        return locale
    }


    fun toLatitude(locationName: String = "Koblenz", context: Context): String {
        val cAddressList: java.util.ArrayList<Address>
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
        var lcLatitude: String = ""
        try {
            cAddressList =
                geocoder.getFromLocationName(locationName, 1) as java.util.ArrayList<Address>
            if (cAddressList.isNotEmpty() && cAddressList.size != 0) {
                lcLatitude = cAddressList.get(0).latitude.toString()
            }
            if (cAddressList.size == 0) return "0"
        } catch (e: IOException) {
            Log.e(TAG, resultMessage, e)
        }
        return lcLatitude
    }


    fun toLongitude(locationName: String = "Koblenz", context: Context): String {
        val cAddressList: java.util.ArrayList<Address>
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
        var lcLongitude: String = ""
        try {
            cAddressList =
                geocoder.getFromLocationName(locationName, 1) as java.util.ArrayList<Address>
            if (cAddressList.isNotEmpty() && cAddressList.size != 0) {
                lcLongitude = cAddressList.get(0).longitude.toString()
            }
            if (cAddressList.size == 0) return "0"
        } catch (e: IOException) {
            Log.e(TAG, resultMessage, e)
        }

        return lcLongitude
    }


}
