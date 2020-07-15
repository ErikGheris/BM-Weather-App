package com.example.bmweather

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.security.AccessController.getContext

class Location {

    val TAG = "PermissionDemo"

    /*  these two have to be declare/initialised @Top */
    var permissionsList = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val permissionsList_request_Code = 10

    fun showToast(mContext: Context?, message: String?) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    fun makeMultipleRequest(activity: MainActivity) {
        ActivityCompat.requestPermissions(
            activity,
            permissionsList,
            permissionsList_request_Code
        )
    }


    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray, context: Context
    ) {
        when (requestCode) {
            permissionsList_request_Code -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                    Toast.makeText(
                        context, "Data Updated",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }

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
        /*
         Only if on the first start, the user clicks on deny, the next
         time he starts the app an intro Dialog will be shown to explain the reason
        for asking permissions, then when he clicks on "OK",
        once again he will be asked for permission.

         */
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

}








