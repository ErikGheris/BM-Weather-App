package com.example.bmweather.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Akash Kumar on 3/28/2020.
 * https://github.com/eduxcellence
 * akkr2017@gmail.com
 */
class RetrofitBuilder {



    companion object {
        private var BASE_URL = "https://api.openweathermap.org/"

        fun getRetrofitInstance(): Retrofit {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}