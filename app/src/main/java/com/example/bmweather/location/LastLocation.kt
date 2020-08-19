package com.example.bmweather.location

import  android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bmweather.FetchWeatherData
import com.example.bmweather.FetchWeatherData.FetchWeatherData.TAG
import com.example.bmweather.MainActivity
import com.example.bmweather.R
import com.example.bmweather.utility.Load
import com.google.android.gms.location.*
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime
import kotlin.time.seconds
import kotlin.time.toDuration

// TODO: 17.08.20 Implement the following interface to write override onRequestPermissionResult here and not in MainActivity anymore
//:ActivityCompat.OnRequestPermissionsResultCallback
class LastLocation(context: Context) {
    val permissionsRequestCode = 10
    private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
    private var resultMessage = "SKSKSK"
    private var bLocation = false
    val tag = "PermissionDemo"
    val PASSED_CONTEXT = context
    val load: Load = Load()
    val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    val geocode = Geocoder(context, Locale.getDefault())
/*
val addressListOfCurrentLocation:  ArrayList<Address>
*/


    /*  these two have to be declare/initialised @Top */
    private var permissionsList = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )


    private fun makeMultipleRequest(activity: MainActivity) {
        ActivityCompat.requestPermissions(
            activity,
            permissionsList,
            this.permissionsRequestCode
        )
    }


    fun setupPermissions(context: Context, activity: MainActivity) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED || coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            Log.i(tag, "Permission to record denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Permission to access the Location is required for this app to Show results based on your LAST KNOWN LOCATION.")
                    .setTitle("Permission required")
                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->
                    Log.i(tag, "Clicked")
                    makeMultipleRequest(activity)
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                makeMultipleRequest(activity)
            }
        }
    }

    @Synchronized
    @SuppressLint("MissingPermission")
    fun setUpLocationListener(
        context: Context,
        LocationReceiver: LocationReceiver,
        progressBar: View
    ) {
        load.start(progressBar)

        if (bLocation) return
        //   load.start(progressBar)

        // for getting the current location update after every 10 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(10000).setFastestInterval(10000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    if (locationResult?.lastLocation != null) {
                        for (location in locationResult.locations) {
                            val myAddressList: ArrayList<Address> = geocode.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            ) as ArrayList<Address>
                            LocationReceiver.countryCode = myAddressList[0].countryCode
                            LocationReceiver.locality = myAddressList[0].locality
                            LocationReceiver.xCoordination = location.latitude.toString()
                            LocationReceiver.yCoordination = location.longitude.toString()
                            //   load.done( progressBar)
                        }
                        //  load.done(progressBar)
                    } else {
                        load.done(progressBar)
                        Log.d(TAG, "Location information is not available!")

                    }
                    // Few more things we can do here:
                }
            },
            Looper.myLooper()
        )
        // load.done( progressBar)
        bLocation = true


    }


    @SuppressLint("MissingPermission")
    fun firstRequest(
        LocationReceiver: LocationReceiver,
        progressBar: View
    ) {
        load.start(progressBar)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { mlocation: Location? ->
            if (mlocation != null) {
                LocationReceiver.firstLatitude = mlocation.latitude.toString()
                LocationReceiver.firstLongitude = mlocation.longitude.toString()
            }
        }
        load.done(progressBar)
        fusedLocationProviderClient.lastLocation.addOnFailureListener { }
    }


    @SuppressLint("MissingPermission")
    fun getLastLocation(
        LocationReceiver: LocationReceiver
    )    {

     //  var mlastLocation: Location? = null
            fusedLocationProviderClient?.lastLocation!!.addOnCompleteListener() { task ->
                if (task.isSuccessful && task.result != null) {
                    task.result!!.time
                  val  mlastLocation = task.result
                    LocationReceiver.firstLatitude = mlastLocation?.latitude.toString()
                    LocationReceiver.firstLongitude =mlastLocation?.longitude.toString()




                    LocationReceiver.time = mlastLocation?.time!!

                }
                else
                {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                }
            }
    }


    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(LocationCallback())
    }


    fun toLatitude(locationName: String = "Koblenz"): String {
        val cAddressList: java.util.ArrayList<Address>
        var locationLatitude = ""
        try {
            cAddressList =
                geocoder.getFromLocationName(locationName, 1) as java.util.ArrayList<Address>
            if (cAddressList.isNotEmpty() && cAddressList.size != 0) {
                locationLatitude = cAddressList[0].latitude.toString()

            }
            if (cAddressList.size == 0) return "0"
        } catch (e: IOException) {
            Log.e(tag, resultMessage, e)
        }
        return locationLatitude
    }


    fun toLongitude(locationName: String = "Koblenz"): String {
        val cAddressList: java.util.ArrayList<Address>
        var locationLongitude = ""
        try {

            cAddressList =
                geocoder.getFromLocationName(locationName, 1) as java.util.ArrayList<Address>
            if (cAddressList.isNotEmpty() && cAddressList.size != 0) {
                locationLongitude = cAddressList[0].longitude.toString()
            }
            if (cAddressList.size == 0) return "0"
        } catch (e: IOException) {
            Log.e(tag, resultMessage, e)
        }

        return locationLongitude
    }


    fun getCountryCodeFromName(locationName: String = "Koblenz"): String {
        return cityNameReq(locationName)[0].countryCode
    }

    private fun cityNameReq(locationName: String = "Koblenz"): ArrayList<Address> {
        return geocoder.getFromLocationName(locationName, 1) as ArrayList<Address>
    }

    fun getLocaleFromName(locationName: String = "Koblenz"): String {
        var locale = "Default_Locale"
        try {
            locale = cityNameReq(locationName)[0].locality

        } catch (e: IOException) {
            resultMessage = Resources.getSystem().getString(R.string.service_not_available)
            Log.e(tag, resultMessage, e)
        }
        return locale
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
        permissionsRequestCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                     setUpLocationListener(
                        PASSED_CONTEXT, this,
                    )

                    Log.i(
                       tag,
                        "Permission has been denied by user"
                    )
                    Toast.makeText(
                        PASSED_CONTEXT, "Permission has been denied by user",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
           setUpLocationListener(
                        PASSED_CONTEXT, LocationReceiver, progressBar = progressBar
                    )
                    Log.i(
                   tag,
                        "Permission has been granted by user"
                    )
                    Toast.makeText(
                        PASSED_CONTEXT, "Permission has been granted by user",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }*/

}