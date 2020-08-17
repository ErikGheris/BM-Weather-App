package com.example.bmweather.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import com.example.bmweather.R

class ConnectivityManagement(context: Context) {
 private val connectionStatusToast:Toast =  Toast.makeText(context,
  (R.string.no_internet), Toast.LENGTH_SHORT)

 fun networkCheck(context: Context): Boolean {
        val statusCheck = networkAvailabilityStatus(context)
        return if (!statusCheck) {
            connectionStatusToast.show()
            false
        } else
            true
    }

 private fun networkAvailabilityStatus(context: Context): Boolean {
        val result: Boolean
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