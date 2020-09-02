package com.example.nasaimages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_asset.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AssetActivity : AppCompatActivity() {

    var vidUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset)


        getImage()
        assetVideo.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(vidUrl)
            startActivity(i)
        }

    }

    private fun getImage() {

        startFetching()

        NasaApi.retrofitServiceImages.getAsset(intent.getStringExtra("NASA_ID"))
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    //txt.text = "Failure: " + t.message
                    stopFetching()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    var text = response.body()
                    var url: String =
                        JSONObject(text).getJSONObject("collection").getJSONArray("items")
                            .getJSONObject(2).getString("href")
                    if (intent.getStringExtra("MEDIA_TYPE") == "image") {
                        Glide.with(baseContext)
                            .load(url)
                            .placeholder(R.drawable.ic_moon)
                            .into(assetImage)
                    } else {
                        assetImage.visibility = View.INVISIBLE
                        assetVideo.visibility = View.VISIBLE
                        vidUrl = url
                    }
                    stopFetching()
                }
            })


    }

    private fun stopFetching() {
        loading3.visibility = View.GONE
    }

    private fun startFetching() {
        loading3.visibility = View.VISIBLE
    }

}