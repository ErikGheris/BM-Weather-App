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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bmweather.LocationReceiver
import com.example.bmweather.MainActivity
import com.example.bmweather.R
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*


class LastLocation {
    var resultMessage = "SKSKSK"
    private val TAG = "PermissionDemo"
    lateinit var mLastLocation: Location
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    val REQUEST_LOCATION_PERMISSION = 87
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


    @SuppressLint("MissingPermission")
    fun setUpLocationListener(

        context: Context,
        LocationReceiver: LocationReceiver
    ) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)


            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        var myAddressList: java.util.ArrayList<Address>
        val mygeocoder: Geocoder = Geocoder(context, Locale.getDefault())
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        myAddressList = mygeocoder.getFromLocation(location.latitude,location.longitude,1) as java.util.ArrayList<Address>
                        /* latTextView.text = location.latitude.toString()
                          lngTextView.text = location.longitude.toString()*/
                        LocationReceiver.countryCode = myAddressList.get(0).countryCode
                        LocationReceiver.locality = myAddressList.get(0).locality
                        LocationReceiver.xCoordination = location.latitude.toString()
                        LocationReceiver.yCoordination = location.longitude.toString()

                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )
    }


    fun toLatitude(locationName: String = "Koblenz", context: Context): String {
        val cAddressList: java.util.ArrayList<Address>
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
        var lcLatitude: String = ""
        try {
            cAddressList = geocoder.getFromLocationName(locationName, 1) as java.util.ArrayList<Address>
            if (cAddressList.isNotEmpty() && cAddressList.size != 0) {
                lcLatitude = cAddressList.get(0).latitude.toString()
            }
            if (cAddressList.size == 0) return "0"
        } catch (e: IOException) {
         //   resultMessage = this.getString(R.string.service_not_available)
            Log.e(TAG, resultMessage, e)
        }
        return lcLatitude
    }


    fun toLongitude(locationName: String = "Koblenz",context: Context): String {
        val cAddressList: java.util.ArrayList<Address>
        val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
        var lcLongitude: String = ""
        try {
            cAddressList = geocoder.getFromLocationName(locationName, 1) as java.util.ArrayList<Address>
            if (cAddressList.isNotEmpty() && cAddressList.size != 0) {
                lcLongitude = cAddressList.get(0).longitude.toString()
            }
            if (cAddressList.size == 0) return "0"
        } catch (e: IOException) {
           // resultMessage = this.getString(R.string.service_not_available)
            Log.e(TAG, resultMessage, e)
        }

        return lcLongitude
    }






}