package com.example.nasaimages

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.nasa.gov/"
private const val BASE_URL_IMAGES = "https://images-api.nasa.gov/"

private const val API_KEY = "KeYD0IrBrbw1xvqf81J5n6VgvaQzYVqfnfB5Y6oM"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

private val retrofitImages = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL_IMAGES)
    .build()

interface NasaApiService {
    @GET("planetary/apod?api_key=$API_KEY")
    fun getTodaysPic(): Call<String>

    @GET("planetary/apod?api_key=$API_KEY")
    fun getDatePic(@Query("date") date: String): Call<String>

    @GET("search")
    fun searchForKeyword(@Query("q") searchKey: String): Call<String>

    @GET("asset/{nasa_id}")
    fun getAsset(@Path("nasa_id") id: String): Call<String>
}


object NasaApi {
    val retrofitService: NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
    val retrofitServiceImages: NasaApiService by lazy {
        retrofitImages.create(NasaApiService::class.java)
    }
}
