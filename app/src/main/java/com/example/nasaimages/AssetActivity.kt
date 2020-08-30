package com.example.nasaimages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_asset.*
import kotlinx.android.synthetic.main.main_fragment.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset)


        getImage()

    }

    private fun getImage(){

        //startFetching()

        NasaApi.retrofitServiceImages.getAsset(intent.getStringExtra("NASA_ID")).enqueue( object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                //txt.text = "Failure: " + t.message
                //stopFetching()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                var text = response.body()
                var url: String = JSONObject(text).getJSONObject("collection").getJSONArray("items")
                    .getJSONObject(0).getString("href")
                Glide.with(baseContext)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(assetImage)
                //stopFetching()
            }
        })



    }

}