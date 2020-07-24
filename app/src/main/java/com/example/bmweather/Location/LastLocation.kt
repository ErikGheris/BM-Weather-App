package com.example.bmweather.Location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.widget.TextView
import com.example.bmweather.MainActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LastLocation {


    @SuppressLint("MissingPermission")
    fun setUpLocationListener(latTextView: TextView, lngTextView: TextView, activity: MainActivity, context: Context) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
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