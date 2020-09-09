package com.example.bmweather

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmweather.adapter.HourlyArrayAdapter
import com.example.bmweather.databinding.ActivityMainBinding
import com.example.bmweather.location.LastLocation
import com.example.bmweather.location.LocationReceiver
import com.example.bmweather.network.ConnectivityManagement
import com.example.bmweather.openweathermap.FetchWeatherData
import com.example.bmweather.openweathermap.response.Current
import com.example.bmweather.openweathermap.response.Daily
import com.example.bmweather.openweathermap.response.Hourly
import com.example.bmweather.utility.Load
import com.example.bmweather.utility.Utility
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),
    LocationReceiver {
    override var xCoordination: String = ""
    override var yCoordination: String = ""
    override var firstLatitude: String = ""
    override var firstLongitude: String = ""
    override var time: Long = 0
    override var countryCode: String = ""
    override var locality: String = ""
    private var searchedXCoordination = ""
    private var searchedYCoordination = ""
    private val apiKey = "6133b390a077c487bc9ac43311b3ba26"
    private var cityName = ""
    private var lastCityCache = cityName
    private var searched: String = ""
    private var exclude = "minutely"
    private val fetchWeather = FetchWeatherData
    private lateinit var lastLocation: LastLocation
    private lateinit var binding: ActivityMainBinding
    private var searching = false
    private var load: Load = Load()
    private lateinit var myUtilities: Utility
    lateinit var connectivityManagement: ConnectivityManagement
    val debugTag = "THISISBS"

    // TODO: 12.08.20   (reason: )lazy declarataion vs inFunctionDeclaration


    //  val list : ArrayList by lazy { ArrayList() }
    // private lateinit var backToast: Toast
    val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
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
        myUtilities = Utility(binding)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val mainActivityContext = applicationContext
        lastLocation = LastLocation(mainActivityContext)
        connectivityManagement = ConnectivityManagement(mainActivityContext)
        lastLocation.setupPermissions(this, this)
        searchButtonAction()
        swipeAction()
        activityButtonAction()
        binding.searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (lastLocation.isLocationEnabled(this@MainActivity)) {
                    searched = binding.searchInput.query.toString()
                    searching = true
                    if (searched.trim().isNotEmpty()) {
                        lastCityCache = cityName
                        cityName = binding.searchInput.query.toString()
                        connectionCheck()
                    } else {
                        Toast.makeText(
                            this@MainActivity, "Please enter a Location!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.location_services_not_enabled),
                        Toast.LENGTH_SHORT
                    ).show()
                    closeKeyboard()
                    showLocationIsDisabledAlert(this@MainActivity)
                }


                val mySValue = binding.searchInput.query.toString()
                Log.i("TxT", "$mySValue")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                Log.i("TxT", "Press querytextchange")
                return false
            }
        })
    }

      fun wipeOff() {
        val myList = listOf(
            binding.description,
            binding.city,
            binding.daytemp,
            binding.mainTemp,
            binding.feelslike,
            binding.humText,
            binding.windText,
            binding.sunriseText,
            binding.sunsetText
        )
        myUtilities.clearAllTextViews(myList)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onRestart() {
        super.onRestart()


        lastLocation.setUpLocationListener(
            this, this, binding.Progress
        ) {
            Log.i(debugTag, "  onResume")
            if (lastLocation.isLocationEnabled(this)) {
                Log.i(debugTag, "location request is sent onResume")
                if (!searching) {
                    makeCurrentLocationWeatherRequest()
                    Log.i(debugTag, "location request is sent onResume: !searching")

                    lastLocation.setUpLocationListener(this, this, binding.Progress) {
                        Log.i("THISISBS", "$longitude and $latitude are the coordinates ")
                        makeCurrentLocationWeatherRequest()
                    }

                } else {
                    Log.i(debugTag, "location request is sent onResume: else")
                    makeSearchWeatherRequest()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        lastLocation.setUpLocationListener(
            this, this, binding.Progress
        ) {
            Log.i(debugTag, "  onResume")
            if (lastLocation.isLocationEnabled(this)) {
                Log.i(debugTag, "location request is sent onResume")
                if (!searching) {
                    makeCurrentLocationWeatherRequest()
                    Log.i(debugTag, "location request is sent onResume: !searching")
                    lastLocation.setUpLocationListener(this, this, binding.Progress) {
                        Log.i(debugTag, "$longitude and $latitude are the coordinates ")
                        makeCurrentLocationWeatherRequest()
                    }
                } else {
                    Log.i(debugTag, "location request is sent onResume: else")
                    makeSearchWeatherRequest()
                }
            }
        }
    }

    fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun showLocationIsDisabledAlert(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.enable_gps))
            .setMessage(context.getString(R.string.required_for_this_app))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
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

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun searchButtonAction() {
        binding.searchButton.setOnClickListener {
            if (lastLocation.isLocationEnabled(this)) {

                searched = binding.searchInput.query.toString()
                searching = true
                if (searched.trim().isNotEmpty()) {
                    lastCityCache = cityName
                    cityName = searched
                    connectionCheck()
                } else {
                    Toast.makeText(
                        this, "Please enter a Location!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.location_services_not_enabled),
                    Toast.LENGTH_SHORT
                ).show()
                showLocationIsDisabledAlert(this)
            }
            closeKeyboard()
        }
    }


    private fun connectionCheck() {
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
        } else {
            Toast.makeText(
                this,
                getString(R.string.no_internet),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun swipeAction() {
        binding.swipe.setOnRefreshListener {
            if (lastLocation.isLocationEnabled(this)) {
                searching = false
                if (connectivityManagement.networkCheck(this)) {
                    makeCurrentLocationWeatherRequest()
                    Toast.makeText(
                        this, "Data Updated, Coordinates are $xCoordination, $yCoordination",
                        Toast.LENGTH_SHORT
                    ).show()
                    //  binding.city.text = getString(R.string.City, locality, countryCode)
                    // Hide swipe to refresh icon animation
                    swipe.isRefreshing = false
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                    swipe.isRefreshing = false
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.location_services_not_enabled),
                    Toast.LENGTH_SHORT
                ).show()
                swipe.isRefreshing = false
                showLocationIsDisabledAlert(this)
            }

        }
    }

    private fun setSearchedCoordinates() {
        searchedXCoordination = lastLocation.toLatitude(cityName)
        searchedYCoordination = lastLocation.toLongitude(cityName)
    }


    private fun makeSearchWeatherRequest() {
        val lang = preferences.getString("reply", "metric")
        fetchWeather.getCurrentWeatherReport(
            apiKey,
            lat = searchedXCoordination,
            lon = searchedYCoordination,
            lang = Locale.getDefault().language,
            units = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("reply", """metric""").toString(),
            exclude = exclude,
            mainActivity = this,
            progressBar = binding.Progress
        )
    }

    private fun makeCurrentLocationWeatherRequest() {
        val lang = preferences.getString("reply", "metric")
        fetchWeather.getCurrentWeatherReport(
            app_id = apiKey,
            lat = xCoordination,
            lon = yCoordination,
            lang = Locale.getDefault().language,
            units = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("reply", """metric""").toString(),
            exclude = exclude,
            mainActivity = this,
            progressBar = binding.Progress
        )
    }

    private fun activityButtonAction() {
        binding.activityButton.setOnClickListener {
            if (lastLocation.isLocationEnabled(this)) {
                val intent = Intent(this, SecondActivity::class.java)
                if (!searching) {
                    intent.putExtra("xCoordination", xCoordination)
                    intent.putExtra("yCoordination", yCoordination); } else {
                    intent.putExtra("xCoordination", searchedXCoordination)
                    intent.putExtra("yCoordination", searchedYCoordination)
                }
                Toast.makeText(
                    this,
                    "wait a sec... ",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intent)
            } else
                Toast.makeText(
                    this,
                    getString(R.string.location_services_not_enabled),
                    Toast.LENGTH_SHORT
                ).show()
        }

    }


    fun uiUtility() {
        load.done(binding.Progress)
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (!searching) {
            binding.city.text = getString(R.string.City, locality, countryCode)
        } else {
            setSearchedCityInfoInTV()
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


    @SuppressLint("SimpleDateFormat")
    private val sunformat = SimpleDateFormat("HH:mm")

    fun current(main: Current) {
        binding.mainTemp.text =
            main.temp.roundToInt().toString().plus(getString(R.string.temp_unit_c))
        binding.sunriseText.text = sunformat.format(main.sunrise * 1000L).toString()
        binding.sunsetText.text = sunformat.format(main.sunset * 1000L).toString()
        binding.humText.text = main.humidity.toString().plus(" %")
        binding.feelslike.text =
            getString(R.string.feels_like_temp).plus(main.feelsLike.roundToInt().toString())
                .plus(getString(R.string.temp_unit_c))
        binding.windText.text = main.windSpeed.roundToInt().toString().plus(getString(R.string.kmh))
        binding.description.text = main.weather[0].description
        Picasso.get()
            .load("http://openweathermap.org/img/wn/" + main.weather[0].icon + "@2x.png")
            .resize(350, 350)
            .into(ic_description)
    }

    fun daily(weather: Daily) {
        binding.daytemp.text =
            weather.temp.min.roundToInt().toString().plus(getString(R.string.temp_unit_c))
                .plus(" / ").plus(
                    weather.temp.max.roundToInt().toString().plus(getString(R.string.temp_unit_c))
                )
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            lastLocation.permissionsRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(
                        "permission granted",
                        "Permission has been granted by user"
                    )
                    Toast.makeText(
                        this, "Permission has been granted by user",
                        Toast.LENGTH_SHORT
                    ).show()
                    when {
                        lastLocation.isLocationEnabled(this) -> {
                            lastLocation.setUpLocationListener(
                                this,
                                this, binding.Progress
                            ) {
                                makeCurrentLocationWeatherRequest()
                                Log.i("THISISBS", "rq")
                            }
                        }
                        else -> {
                            //TODO it should be ajdusted so that when the user comes back after activation# a new rqst will be sent
                            lastLocation.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Log.i(
                        "permissionDenied",
                        "Permission has been denied by user"
                    )
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}


