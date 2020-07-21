package com.example.bmweather

import android.app.Activity
import android.content.res.Resources
import android.text.Editable
import android.widget.AutoCompleteTextView
import android.widget.TextView

//Declare var for Textview with late init
lateinit var weatherDescription: TextView
lateinit var mainTemp: TextView
lateinit var tempAllDay: TextView
lateinit var city: TextView
lateinit var realTemp: TextView


class search {

    //Get textview by id

  //  var weatherDescriptiony = ( as Activity).findViewById(R.id.description)
   /* mainTemp = findViewById(R.id.mainTemp)

    tempAllDay = findViewById(R.id.TempAllDay)

    city = findViewById(R.id.city)

    realTemp = findViewById(R.id.realTemp)
    */




    // a function to read and return the input value of a given autoCompleteTextView
    fun get(autoCompleteTextView: AutoCompleteTextView): Editable = autoCompleteTextView.text



    fun rain(
        weather: Weather

    ) {


        weatherDescription.text = Resources.getSystem().getString(R.string.visaa).plus(weather.description)
    }

    fun temp(main: Main

    ) {
        val string: String = Resources.getSystem().getString(R.string.max_temp)
        mainTemp.text = "".plus(main.temp.toUInt()).plus(Resources.getSystem().getString(R.string.))
    }

    fun tempallday(main: Main
    ) {
        tempAllDay.text = Resources.getSystem().getString(R.string.min_temp).plus(main.temp_min.toUInt()).plus("  ").plus(
            Resources.getSystem().getString(
                R.string.max_temp
            )
        ).plus(main.temp_max.toUInt())
    }


    fun realTemp(main: Main

    ) {
        realTemp.text = Resources.getSystem().getString(R.string.feels_like_temp).plus(main.feels_like.toUInt())
    }

    fun city(weatherResponse: WeatherResponse

    ) {
        city.text =
            weatherResponse.name.plus(Resources.getSystem().getString(R.string.comma)).plus(weatherResponse.sys!!.country)
    }


}