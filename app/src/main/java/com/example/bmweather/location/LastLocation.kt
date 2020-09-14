package com.example.bmweather.location

import  android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bmweather.openweathermap.FetchWeatherData.FetchWeatherData.TAG
import com.example.bmweather.MainActivity
import com.example.bmweather.R
import com.example.bmweather.utility.Load
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import android.provider.Settings;
import com.example.bmweather.openweathermap.mainActivityInstance
import com.google.android.gms.tasks.OnCompleteListener

// TODO: 17.08.20 Implement the following interface to write override onRequestPermissionResult here and not in MainActivity anymore
//:ActivityCompat.OnRequestPermissionsResultCallback
class LastLocation(context: Context) {
    val permissionsRequestCode = 10
    private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
    private var resultMessage = "SKSKSK"
    private var bLocation = false
    val debugTag = "THISISBS"
    val tag = "PermissionDemo"
    val PASSED_CONTEXT = context
    val load: Load = Load()
    private val fusedLocationProviderClient: FusedLocationProviderClient =
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

    fun isLocationServiceEnabled(context: Context): Boolean {

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        return if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder(context)
                .setMessage(R.string.gps_network_not_enabled)
                .setNegativeButton(R.string.Cancel, null)
                .setPositiveButton(
                    R.string.open_location_settings
                ) { dialogInterface_, int ->
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
            true
        } else
            false

    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.enable_gps))
            .setMessage(context.getString(R.string.required_for_this_app))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }


    fun permissionRequests(activity: MainActivity) {
        ActivityCompat.requestPermissions(
            activity,
            permissionsList,
            this.permissionsRequestCode
        )
    }


/*
    fun setupPermissions(context: Context, activity: MainActivity) {
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

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
                builder.setPositiveButton("OK") { _, _ ->
                    Log.i(tag, "Clicked")
                    // ask for requests again
                    permissionRequests(activity)
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                permissionRequests(activity)
            }
        } else {
            permissionRequests(activity)
        }
    }
*/

    fun setupPermissions(context: Context, activity: MainActivity) {
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED || coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            permissionRequests(activity)
        } else {
            Log.i(tag, "Permission to record denied")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                retryRequest(context, activity)
            } else {
                permissionRequests(activity)
            }
        }
    }

    private fun retryRequest(
        context: Context,
        activity: MainActivity
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Permission to access the Location is required for this app to Show results based on your LAST KNOWN LOCATION.")
            .setTitle("Permission required")
        builder.setPositiveButton("OK") { _, _ ->
            Log.i(tag, "Clicked")
            // ask for requests again
            permissionRequests(activity)
        }
        val dialog = builder.create()
        dialog.show()
    }


    @Synchronized
    @SuppressLint("MissingPermission")
    fun setUpLocationListener(
        context: Context,
        LocationReceiver: LocationReceiver,
        progressBar: View,
        expression: (() -> Unit)
    ) {
        Log.i(debugTag, "location listening ")
        load.start(progressBar)
/*
        if (bLocation) {
            Log.i(debugTag, "return ")
            load.done(progressBar)
            return
         }*/
        //   load.start(progressBar)
        // for getting the current location update after every 10 seconds with high accuracy
        val locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        Log.i(debugTag, "not ret ")
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult?) {
                    Log.i(debugTag, "location CallBack")
                    super.onLocationResult(locationResult)
                    Log.i(debugTag, "callBack ")
                    if (locationResult?.lastLocation != null) {
                        Log.i(debugTag, "runn ,ing")
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
                            Log.i(debugTag, "${location.longitude} , ${location.latitude}")
                            expression.invoke()
                        }
                        //  load.done(progressBar)
                    } else {
                     //   mainActivityInstance.imageIsInvisible=false
                        mainActivityInstance.wipeTextsOff(mainActivityInstance.getTextViewList())
                        Log.i(debugTag, "location is null ")
                        load.done(progressBar)
                        Log.d(TAG, "Location information is not available!")

                    }
                    // Few more things we can do here:
                    Log.i(debugTag, "location loop is going on ")
                }
            },
            Looper.myLooper()
        )
        // load.done( progressBar)
/*        bLocation = true*/


    }


    @SuppressLint("MissingPermission")
    fun firstRequest(
        LocationReceiver: LocationReceiver,
        progressBar: View, expression: (() -> Unit)
    ) {
        load.start(progressBar)
        Log.i(debugTag, " progress spinner is activated")
        fusedLocationProviderClient.locationAvailability.addOnCompleteListener {
            OnCompleteListener<LocationAvailability?> {
                /*         object :
                OnCompleteListener<LocationAvailability?> {
                override fun onComplete(p0: Task<LocationAvailability?>) {

*/
                Log.i(debugTag, " complete listening")


            }

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { mlocation: Location? ->
                Log.i(debugTag, "listening !!")
                /* if (mlocation != null) {
                LocationReceiver.firstLatitude = mlocation.latitude.toString()
                LocationReceiver.firstLongitude = mlocation.longitude.toString()
                Log.i("first","${mlocation.longitude} , ${mlocation.latitude}")
                expression.invoke()
            }*/



                if (mlocation != null) {
                    Log.i(debugTag, "runn ,ing")
                    val myAddressList: ArrayList<Address> = geocode.getFromLocation(
                        mlocation.latitude,
                        mlocation.longitude,
                        1
                    ) as ArrayList<Address>
                    LocationReceiver.countryCode = myAddressList[0].countryCode
                    LocationReceiver.locality = myAddressList[0].locality
                    LocationReceiver.xCoordination = mlocation.latitude.toString()
                    LocationReceiver.yCoordination = mlocation.longitude.toString()
                    Log.i(debugTag, "${mlocation.longitude} , ${mlocation.latitude}")
                    expression.invoke()

                    //  load.done(progressBar)
                } else {
                    Log.i(debugTag, "location is null ")
                    load.done(progressBar)
                    Log.d(TAG, "Location information is not available!")

                }

            }
            // load.done(progressBar)
            fusedLocationProviderClient.lastLocation.addOnFailureListener {

                Log.i(debugTag, " failed ")
                load.done(progressBar)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(
        LocationReceiver: LocationReceiver, expression: (() -> Unit)
    ) {


        //  var mlastLocation: Location? = null

        fusedLocationProviderClient?.lastLocation!!.addOnCompleteListener() { task ->
            if (task.isSuccessful && task.result != null) {
                task.result!!.time
                val mlastLocation = task.result
                LocationReceiver.firstLatitude = mlastLocation?.latitude.toString()
                LocationReceiver.firstLongitude = mlastLocation?.longitude.toString()
                LocationReceiver.time = mlastLocation?.time!!
                expression.invoke()


            } else {
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
            if (cAddressList.size == 0) return "null"
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
            if (cAddressList.size == 0) return "null"
        } catch (e: IOException) {
            Log.e(debugTag, resultMessage, e)

        }
        Log.i(debugTag," name to Coor® $locationLongitude")
        return locationLongitude
    }

    fun getCountryCodeFromName(locationName: String = "Koblenz"): String {

        try {
            if (cityNameReq(locationName).size != 0) {
                return cityNameReq(locationName)[0].countryCode.toString()
            }

        } catch (e: Exception) {
            resultMessage = Resources.getSystem().getString(R.string.service_not_available)
            Log.e("GETLOCALEFROMNAME", resultMessage, e)
        }

        return "null"
    }

    private fun cityNameReq(locationName: String = "Koblenz"): ArrayList<Address> {
        return geocoder.getFromLocationName(locationName, 1) as ArrayList<Address>
    }

    fun getLocaleFromName(locationName: String = "Koblenz"): String {

        try {
            if (cityNameReq(locationName).size != 0){
          return  cityNameReq(locationName)[0].locality.toString()
 }
        } catch (e: Exception) {
            resultMessage = Resources.getSystem().getString(R.string.service_not_available)
            Log.e(debugTag, resultMessage, e)
        }
        return "null"
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