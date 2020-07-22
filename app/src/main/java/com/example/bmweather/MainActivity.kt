package com.example.bmweather


import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bmweather.ResponseModel.current.Weather
import com.example.bmweather.ResponseModel.current.WeatherReport
import com.example.bmweather.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Runnable
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import source.open.akash.mvvmlogin.Model.nextdayforecast.ListData

class MainActivity : AppCompatActivity() {

    //Declare var for Textview with late init
    private lateinit var weatherDescription: TextView
    private lateinit var mainTemp: TextView
    private lateinit var tempAllDay: TextView
    private lateinit var city: TextView
    private lateinit var realTemp: TextView
    lateinit var forecast: TextView



    // Declare parameters for tge GET funktion
    val BaseUrl = "http://api.openweathermap.org/"
    val app_id = "6133b390a077c487bc9ac43311b3ba26"
    var cityName = "Berlin"
    var units = "metric"
    var lang = "de"
    var lastCityCache = cityName
    var searched: String = ""
    var cnt = "3"

    var fetchWeather = FetchWeatherData
    lateinit var binding: ActivityMainBinding
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

        }

        // clears the autoCompleteTExtView when it is clicked
        search_input.setOnClickListener {
            search_input.setText("")
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


            //finish();
            //overridePendingTransition( 0, 0);
            startActivity(intent);
            // overridePendingTransition( 0, 0);*/
        }


        //Get textview by id

        weatherDescription = findViewById(R.id.description)

   

     fun delayHandler() {
        val handler = Handler()
        handler.postDelayed(Runnable {
            //wait x delay MS and then progress is done
            Load().done(progress_widget)
        }, 1000) // 1000 milliseconds

        forecast = findViewById(R.id.forecast)

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