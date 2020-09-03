package com.example.nasaimages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    var jsonTxt = MutableLiveData<String>()
    var failureText = MutableLiveData<String>()
    var mediaDetails = MutableLiveData<String>()
    var isLoading = MutableLiveData<Boolean>()

    init {
        getImage()
        failureText.value = ""
        mediaDetails.value = "Image of the day"
        isLoading.value = true
    }


    private fun getImage() {

        startFetching()

        NasaApi.retrofitService.getTodaysPic().enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                failureText.value = "Failure: " + t.message
                stopFetching()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.body() != null) {
                    jsonTxt.value = (response.body())
                    mediaDetails.value = JSONObject(jsonTxt.value).getString("explanation")
                }
                stopFetching()
            }
        })


    }

    private fun stopFetching() {
        isLoading.value = false
    }

    private fun startFetching() {
        isLoading.value = true
    }

    fun getDateImage(date: String) {

        startFetching()

        NasaApi.retrofitService.getDatePic(date).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                failureText.value = "Failure: " + t.message
                stopFetching()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.body() != null) {
                    jsonTxt.value = (response.body())
                    mediaDetails.value = JSONObject(jsonTxt.value).getString("explanation")
                }
                stopFetching()
            }
        })


    }
}