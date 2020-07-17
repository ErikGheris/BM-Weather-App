package com.example.bmweather


import android.Manifest
import android.content.Context
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
import android.location.LocationManager
import android.os.Looper
import android.text.Editable
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    //Declare var for Textview with late init
    private lateinit var weatherDescription: TextView
    private lateinit var mainTemp: TextView
    private lateinit var tempAllDay: TextView
    private lateinit var city: TextView
    private lateinit var realTemp: TextView


    var fusedLocationClient: FusedLocationProviderClient? = null


    // Declare parameters for tge GET funktion


    private val BaseUrl = "http://api.openweathermap.org/"
    private val APIKey = "6133b390a077c487bc9ac43311b3ba26"
    private var cityName = "Berlin"
    var units = "metric"
    var lang = "de"
    var lastCityCache = cityName
 var searched: String   = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


/*
        fun onCreateOptionsMenu(menu: Menu?): Boolean {
            return super.onCreateOptionsMenu(menu)
            menuInflater.inflate(R.menu.main, menu)
            return super.onCreateOptionsMenu(menu)
        }*/







        location_button.setOnClickListener {
           // Location().setupPermissions(this, this)
            isLocationEnabled(this)
            setUpLocationListener()
        }


        /*location_button.setOnClickListener {
                 Toast.makeText(
                      this, "IT WORKS",
                      Toast.LENGTH_SHORT
                  ).show()
        }
*/

        //toaster Message + get current data
        search_button.setOnClickListener {
            searched = search().get(search_input).toString()
            if (searched.trim().isNotEmpty()) {

      lastCityCache     =        cityName
                cityName = searched
                getCurrentData()
                //safe city
                Toast.makeText(this, "looking for $searched's Weather Info", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(
                    this, "Please enter a Location!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // clears the autoCompleteTExtView when it is clicked
        search_input.setOnClickListener {
            search_input.setText("")
        }

        // all about pull to refresh data
        swipe.setOnRefreshListener {


       /*     // if (response.code.get != 404) {

           // activityRestart()
            Toast.makeText(
                applicationContext, "City NoT FounD",
                Toast.LENGTH_SHORT
            ).show()

            //}*/

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
                            applicationContext, "City NoT FounD",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
            }

            private fun correctResponse(response: Response<WeatherResponse>) {
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


            /*       private fun badResponse(response: Response<WeatherResponse>) {
             *//*          val weatherResponse = response.body()!!
                //Build String from response
                val stringBuilder = null
             //   weatherDescription.text = stringBuilder
                *//**//*       rain(weatherResponse.weather[0])
                        temp(weatherResponse.main!!)
                        tempallday(weatherResponse.main!!)*//**//*
                 //       realTemp(weatherResponse.main!!)*//*
                         wrong()
            }
*/


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


    private fun sorryDisplayView() {
        city.text = getString(R.string.sorry)
        realTemp.text = getString(R.string.empty)
        tempAllDay.text = getString(R.string.empty)
        weatherDescription.text = getString(R.string.empty)
        mainTemp.text = getString(R.string.empty)
    }


    // Declare parameters for tge GET funktion



    private fun activityRestart() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Location().permissionsList_request_Code -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {


                    setUpLocationListener()


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

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    //  fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        latTextView.text = location.latitude.toString()
                        lngTextView.text = location.longitude.toString()
                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )


    }


}
