package com.example.bmweather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bmweather.R
import com.example.bmweather.openweathermap.response.Hourly
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.hourly_item.view.*
import java.text.SimpleDateFormat
import kotlin.math.roundToInt


class HourlyArrayAdapter(val hourly : List<Hourly>) : RecyclerView.Adapter<HourlyArrayAdapter.HourlyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        return HourlyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.hourly_item, parent, false)
        )
    }

    override fun getItemCount() = hourly.size

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourly = hourly[position]
        holder.bind(hourly)

    }


    class HourlyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SimpleDateFormat")
        private val format = SimpleDateFormat("HH:mm" )
        val hourly_pic = view.findViewById<ImageView>(R.id.hourly_pic)
        fun bind (hourly: Hourly) {
            view.temp.text = hourly.temp.roundToInt().toString().plus(" °")
            view.time.text = format.format(hourly.dt * 1000L).toString()
            Picasso.get().load("http://openweathermap.org/img/wn/" + hourly.weather[0].icon + "@2x.png").into(hourly_pic)
        }
    }
}
