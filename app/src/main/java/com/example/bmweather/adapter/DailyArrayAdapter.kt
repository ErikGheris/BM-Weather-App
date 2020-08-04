package com.example.bmweather.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bmweather.FetchWeatherData
import com.example.bmweather.Fragments.CurrentWeather
import com.example.bmweather.Fragments.Forecast
import com.example.bmweather.MainActivity
import com.example.bmweather.R
import com.example.bmweather.response.Daily
import com.example.bmweather.response.Weather
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.fragment_current_weather.*
import java.text.SimpleDateFormat


class DailyArrayAdapter
    (
    context: Context, resource: Int, objects: ArrayList<Daily>
) : ArrayAdapter<Daily>(context, resource, objects) {
    private val rentalProperties: ArrayList<Daily> = objects



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val property = rentalProperties[position]
        val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.daily_item, null)
        val view2 = inflater.inflate(R.layout.fragment_forecast, null)
        val des_pic = view.findViewById<ImageView>(R.id.des_pic)
        val format = SimpleDateFormat("EEEE',' dd.MM.yy" )

        view.findViewById<TextView>(R.id.temp).text = context.getString(R.string.min_temp).plus(property.temp.min.toUInt().toString()).plus(context.getString(R.string.empty)).plus(context.getString(R.string.max_temp)).plus(property.temp.max.toUInt().toString())
        Picasso.get().load("http://openweathermap.org/img/wn/" + property.weather[0].icon + "@2x.png").into(des_pic)
        view.findViewById<TextView>(R.id.day).text = format.format(property.dt * 1000L).toString()


        return view
    }


}



