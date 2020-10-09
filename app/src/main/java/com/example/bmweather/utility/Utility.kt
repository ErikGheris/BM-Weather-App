package com.example.bmweather.utility

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bmweather.MainActivity
import com.example.bmweather.MySuggestionProvider
import com.example.bmweather.SearchRecentSuggestions
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar

//val binding: ActivityMainBinding
class Utility {
    private val tag = "Utility"
    fun makeScreenUntouchable(window: Window) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }


    fun longToastMsg(context: Context, stringID: Int) {
        Toast.makeText(
            context,
            context.getString(stringID),
            Toast.LENGTH_LONG
        ).show()
    }


    fun makeSnackbar(view: View, stringId: Int, duration: Int) {
        val mySnack = Snackbar.make(view, stringId, duration)
        mySnack.show()
        mySnack.setAction("Dismiss") {
            mySnack.dismiss()
        }
        mySnack.animationMode = ANIMATION_MODE_SLIDE
    }

    fun locationPermissionsAvailable(context: Context, activity: MainActivity): Boolean {
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
            )
                return false
        }
        return true
    }

    private fun clearTextView(textView: TextView) {
        textView.text = ""
    }

    fun clearAllTextViews(list: List<TextView>) {
        list.map {
            clearTextView(it)
        }
    }

    private fun TextView.utilities() {
        this.text = ""
    }

    private fun clearInputText(textView: AutoCompleteTextView) {
        textView.setText("")
    }

/*

fun makePermissionsDialog(){
    val builder = android.app.AlertDialog.Builder(this)
    //set title for alert dialog
    builder.setTitle(R.string.missingPermissionsDialogTitle)
    //set message for alert dialog
    builder.setMessage(R.string.missingPermissionsdialogMessage)
    builder.setIcon(android.R.drawable.ic_dialog_alert)

    //performing positive action
    builder.setPositiveButton("Yes"){dialogInterface, which ->
        Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
    }
    //performing cancel action
    builder.setNeutralButton("Cancel"){dialogInterface , which ->
        Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
    }
    //performing negative action
    builder.setNegativeButton("No"){dialogInterface, which ->
        Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
    }
    // Create the AlertDialog
    val alertDialog: android.app.AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()

}
*/


    fun makeAlertDialog(
        context: Context,
        dialogMessageStringId: Int,
        titleStringId: Int,
        clearableHistory: android.provider.SearchRecentSuggestions
    ) {
        val builder = android.app.AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle(titleStringId)
        //set message for alert dialog
        builder.setMessage(dialogMessageStringId)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
          clearableHistory.clearHistory()
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            return@setNegativeButton
        }
        // Create the AlertDialog
        val alertDialog: android.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }
}