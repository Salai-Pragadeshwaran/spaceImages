package com.example.studc.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nasaimages.NasaApi
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    var jsonTxt = MutableLiveData<String>()
    var failureText = MutableLiveData<String>()

    init {
        getImage()
        //jsonTxt.value = JSONObject("")
        failureText.value = ""
    }


    private fun getImage() {

        //startFetching()

        NasaApi.retrofitService.getTodaysPic().enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                failureText.value = "Failure: " + t.message
                //stopFetching()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                jsonTxt.value = (response.body())
                //stopFetching()
            }
        })


    }

    fun getDateImage(date: String) {

        //startFetching()

        NasaApi.retrofitService.getDatePic(date).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                failureText.value = "Failure: " + t.message
                //stopFetching()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                jsonTxt.value = (response.body())
                //stopFetching()
            }
        })


    }
}