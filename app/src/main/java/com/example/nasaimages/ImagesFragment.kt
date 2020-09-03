package com.example.nasaimages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nasaimages.databinding.ImagesFragmentBinding
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ImagesFragment : Fragment() {

    companion object {
        fun newInstance() = ImagesFragment()
    }

    private lateinit var viewModel: ImagesViewModel

    var searchString = ""
    var searchThread = SearchThread()
    var imagesData = ArrayList<ImageData>()

    lateinit var binding: ImagesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.images_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(ImagesViewModel::class.java)

        if (getOrientation() == 1) {
            binding.imageDataRecyclerView.setLayoutManager(
                GridLayoutManager(
                    binding.root.context,
                    2
                )
            )
        } else {
            binding.imageDataRecyclerView.setLayoutManager(
                GridLayoutManager(
                    binding.root.context,
                    4
                )
            )
        }

        if (!searchThread.isAlive) {
            searchThread.start()
        }

        return binding.root
    }

    inner class SearchThread() : Thread() {
        override fun run() {
            while (true) {
                sleep(250)
                try {
                    if (!(searchString.equals(binding.search.text.toString()))) {
                        activity!!.runOnUiThread(
                            object : Runnable {
                                override fun run() {
                                    searchString = binding.search.text.toString()
                                    if (searchString != "") {
                                        getData()
                                    } else {
                                        searchString = "welcome"
                                        getData()
                                    }
                                }
                            }
                        )
                    }

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun getData() {
        startFetching()
        NasaApi.retrofitServiceImages.searchForKeyword(searchString)
            .enqueue(object : Callback<kotlin.String> {
                override fun onFailure(call: Call<kotlin.String>, t: Throwable) {
                    //check.text = "Failure: " + t.message
                    stopFetching()
                }

                override fun onResponse(
                    call: Call<kotlin.String>,
                    response: Response<kotlin.String>
                ) {
                    //TODO: check is response is successful
                    var text = response.body()
                    //check.text = text
                    imagesData.clear()

                    var jsonTxt: JSONArray =
                        JSONObject(text).getJSONObject("collection").getJSONArray("items")
                    var i: Int = 0
                    while (i < jsonTxt.length()) {
                        var imgData = ImageData()
                        imgData.media_type =
                            jsonTxt.getJSONObject(i).getJSONArray("data").getJSONObject(0)
                                .getString("media_type")
                        imgData.nasa_id =
                            jsonTxt.getJSONObject(i).getJSONArray("data").getJSONObject(0)
                                .getString("nasa_id")
                        imgData.title =
                            jsonTxt.getJSONObject(i).getJSONArray("data").getJSONObject(0)
                                .getString("title")
                        if (jsonTxt.getJSONObject(i).has("links")) {
                            imgData.url =
                                jsonTxt.getJSONObject(i).getJSONArray("links").getJSONObject(0)
                                    .getString("href")
                        }
                        imagesData.add(imgData)
                        Log.i("NUMBER", i.toString())
                        ++i
                    }
                    populateRecycler(imagesData)
                    stopFetching()
                }
            })
    }

    private fun startFetching() {
        binding.loading2.visibility = View.VISIBLE
    }

    private fun stopFetching() {
        binding.loading2.visibility = View.GONE
    }

    fun populateRecycler(imagesData: ArrayList<ImageData>) {
        var adapter = ImagesAdapter(imagesData, binding.root.context)
        binding.imageDataRecyclerView.adapter = adapter
    }

    fun getOrientation(): Int {
        return if (resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels) {
            0 // for landscape
        } else {
            1 // for portrait
        }
    }
}