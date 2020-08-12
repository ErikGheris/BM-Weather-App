package com.example.bmweather.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.bmweather.R
import com.example.bmweather.binding
import com.example.bmweather.response.Daily
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.daily_item.view.*
import java.text.SimpleDateFormat
import kotlin.math.roundToInt


class DailyArrayAdapter
    (
    context: Context, resource: Int, objects: ArrayList<Daily>
) : ArrayAdapter<Daily>(context, resource, objects) {
    private val rentalProperties: ArrayList<Daily> = objects


    @SuppressLint("InflateParams", "ViewHolder", "SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val property = rentalProperties[position]
        val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.daily_item, null)
        val desPic = view.findViewById<ImageView>(R.id.des_pic)
        val format = SimpleDateFormat("EEEE',' dd.MM.yy" )

        view.findViewById<TextView>(R.id.temp).text = context.getString(R.string.min_temp).plus(property.temp.min.roundToInt().toString()).plus(context.getString(R.string.empty)).plus(context.getString(R.string.max_temp)).plus(property.temp.max.roundToInt().toString())
        Picasso.get().load("http://openweathermap.org/img/wn/" + property.weather[0].icon + "@2x.png").into(desPic)
        view.findViewById<TextView>(R.id.day).text = format.format(property.dt * 1000L).toString()


        return view

    }


}



