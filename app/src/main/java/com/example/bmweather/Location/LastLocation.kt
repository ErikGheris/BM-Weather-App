package com.example.bmweather.Location

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bmweather.LocationReceiver
import com.example.bmweather.MainActivity
import com.example.bmweather.R
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class LastLocation(context: Context) {
    val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
    var resultMessage = "SKSKSK"
    var location = false
    private val TAG = "PermissionDemo"
    lateinit var mLastLocation: Location
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    val REQUEST_LOCATION_PERMISSION = 87


/*
val addressListOfCurrentLocation:  ArrayList<Address>
*/



    /*  these two have to be declare/initialised @Top */
    private var permissionsList = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val permissionsRequestCode = 10

    fun showToast(mContext: Context?, message: String?) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun makeMultipleRequest(activity: MainActivity) {
        ActivityCompat.requestPermissions(
            activity,
            permissionsList,
            permissionsRequestCode
        )
    }


    fun setupPermissions(context: Context, activity: MainActivity) {
        val FINE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val COARSE_LOCATION_PERMISSION = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (FINE_LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED || COARSE_LOCATION_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
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
                ) { dialog, id ->
                    Log.i(TAG, "Clicked")
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
        LocationReceiver: LocationReceiver


    ) {
        if (location) return

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        // for getting the current location update after every 10 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(10000).setFastestInterval(10000)


            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val geocoder = Geocoder(context, Locale.getDefault())
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        var myAddressList: ArrayList<Address> = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        ) as ArrayList<Address>
                        LocationReceiver.countryCode = myAddressList[0].countryCode
                        LocationReceiver.locality = myAddressList[0].locality
                        LocationReceiver.xCoordination = location.latitude.toString()
                        LocationReceiver.yCoordination = location.longitude.toString()

                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )
        location = true
    }


    fun toLatitude(locationName: String = "Koblenz"): String {
        val cAddressList: java.util.ArrayList<Address>
        var locationLatitude: String = ""
        try {
            cAddressList =
                geocoder.getFromLocationName(locationName, 1) as java.util.ArrayList<Address>
            if (cAddressList.isNotEmpty() && cAddressList.size != 0) {
                locationLatitude = cAddressList.get(0).latitude.toString()

            }
            if (cAddressList.size == 0) return "0"
        } catch (e: IOException) {
            Log.e(TAG, resultMessage, e)
        }
        return locationLatitude
    }


    fun toLongitude(locationName: String = "Koblenz"): String {
        val cAddressList: java.util.ArrayList<Address>
        var locationLongitude: String = ""
        try {

            cAddressList =
                geocoder.getFromLocationName(locationName, 1) as java.util.ArrayList<Address>
            if (cAddressList.isNotEmpty() && cAddressList.size != 0) {
                locationLongitude = cAddressList.get(0).longitude.toString()
            }
            if (cAddressList.size == 0) return "0"
        } catch (e: IOException) {
            Log.e(TAG, resultMessage, e)
        }

        return locationLongitude
    }


    fun getCountryCodeFromName(locationName: String= "Koblenz"): String {
        return cityNameReq(locationName)[0].countryCode
    }

    private fun cityNameReq(locationName: String ="Koblenz"): ArrayList<Address> {
        return geocoder.getFromLocationName(locationName, 1) as ArrayList<Address>
    }

    fun getLocaleFromName(locationName: String = "Koblenz"): String {
    var locale = "Default_Locale"
        try {
          locale = cityNameReq(locationName)[0].locality

        } catch (e: IOException) {
            resultMessage = Resources.getSystem().getString(R.string.service_not_available)
            Log.e(TAG, resultMessage, e)
        }
        return locale
    }


}