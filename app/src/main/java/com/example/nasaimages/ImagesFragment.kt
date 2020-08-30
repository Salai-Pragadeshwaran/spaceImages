package com.example.nasaimages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studc.ui.main.ImagesViewModel
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

    lateinit var searchEditText: EditText
    //lateinit var check: TextView
    lateinit var imgRecycler: RecyclerView
    lateinit var root: View
    var searchString = ""
    var searchThread = SearchThread()
    var imagesData = ArrayList<ImageData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.images_fragment, container, false)

        searchEditText = root.findViewById(R.id.search)
        //check = root.findViewById(R.id.checkText)

        imgRecycler = root.findViewById(R.id.imageDataRecyclerView)
        imgRecycler.setLayoutManager(GridLayoutManager(root.context, 2))

        if (!searchThread.isAlive) {
            searchThread.start()
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ImagesViewModel::class.java)
        // TODO: Use the ViewModel
    }

    inner class SearchThread() : Thread() {
        override fun run() {
            while (true) {
                sleep(1000)
                try {
                    if (!(searchString.equals(searchEditText.text.toString()))) {
                        activity!!.runOnUiThread(
                            object : Runnable {
                                override fun run() {
                                    searchString = searchEditText.text.toString()
                                    getData()
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
        NasaApi.retrofitServiceImages.searchForKeyword(searchString).enqueue( object: Callback<kotlin.String> {
            override fun onFailure(call: Call<kotlin.String>, t: Throwable) {
                //check.text = "Failure: " + t.message
                //stopFetching()
            }

            override fun onResponse(call: Call<kotlin.String>, response: Response<kotlin.String>) {
                var text = response.body()
                //check.text = text
                imagesData.clear()

                var jsonTxt: JSONArray = JSONObject(text).getJSONObject("collection").getJSONArray("items")
                var i : Int = 0
                while(i < jsonTxt.length()){
                    var imgData = ImageData()
                    imgData.media_type = jsonTxt.getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("media_type")
                    imgData.nasa_id = jsonTxt.getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("nasa_id")
                    imgData.title = jsonTxt.getJSONObject(i).getJSONArray("data").getJSONObject(0).getString("title")
                    if (jsonTxt.getJSONObject(i).has("links")) {
                        imgData.url = jsonTxt.getJSONObject(i).getJSONArray("links").getJSONObject(0).getString("href")
                    }
                    imagesData.add(imgData)
                    Log.i("NUMBER", i.toString())
                    ++i
                }
                populateRecycler(imagesData)
                //stopFetching()
            }
        })
    }

    fun populateRecycler(imagesData: ArrayList<ImageData>){
        var adapter = ImagesAdapter(imagesData, root.context)
        imgRecycler.adapter = adapter
    }
}