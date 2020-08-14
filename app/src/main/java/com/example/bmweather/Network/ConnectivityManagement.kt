package com.example.bmweather.Network

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings.Global.getString
import android.provider.Settings.Secure.getString
import android.widget.Toast
import androidx.core.content.res.TypedArrayUtils.getString
import com.example.bmweather.R

class ConnectivityManagement(context: Context) {
 private val connectionStatusToast:Toast =  Toast.makeText(context,
  (R.string.no_internet), Toast.LENGTH_SHORT)


    // TODO: 13.08.20  connectionStatusToast should use :>      Resources.getSystem().getString(R.string.no_internet) as the text

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