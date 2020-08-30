package com.example.nasaimages

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.studc.ui.main.MainViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    lateinit var txt: TextView
    lateinit var iotd: ImageView
    lateinit var video: VideoView

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate( R.layout.main_fragment, container, false)

        txt = root.findViewById(R.id.textViewImg)
        iotd = root.findViewById(R.id.imgOfTheDay)
        video = root.findViewById(R.id.videoOfTheDay)

        var setDate = root.findViewById(R.id.setDate) as Button
        setDate.setOnClickListener {
            var datePicker = DatePickerFragment()
            datePicker.setTargetFragment(this, 0)
            datePicker.show(this!!.fragmentManager!!, "Date Picker")
            //TODO: make sure user doesn't select a future date
            //TODO: load videos
            //TODO: implement pagination
            //TODO: set splash screen
            //TODO: make better UI use lottie animations
        }

        getImage()
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun getImage(){

        startFetching()

        NasaApi.retrofitService.getTodaysPic().enqueue( object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                txt.text = "Failure: " + t.message
                stopFetching()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                var text = response.body()
                var jsonTxt: JSONObject = JSONObject(text)
                txt.text = jsonTxt.getString("explanation")
                if (jsonTxt.getString("media_type")=="image") {
                    video.visibility = View.INVISIBLE
                    iotd.visibility = View.VISIBLE
                    Glide.with(imgOfTheDay.context)
                        .load(jsonTxt.getString("url"))
                        .into(imgOfTheDay)
                }else{
                    video.visibility = View.VISIBLE
                    iotd.visibility = View.INVISIBLE
                    var uri = Uri.parse(jsonTxt.getString("url"))
                    video.setVideoURI(uri)
                    video.start()
                }
                stopFetching()
            }
        })



    }

    private fun getDateImage(date: String){

        startFetching()

        NasaApi.retrofitService.getDatePic(date).enqueue( object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                txt.text = "Failure: " + t.message
                stopFetching()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                var text = response.body()
                var jsonTxt: JSONObject = JSONObject(text)
                txt.text = jsonTxt.getString("explanation")
                if(jsonTxt.getString("media_type")=="image") {
                    video.visibility = View.INVISIBLE
                    iotd.visibility = View.VISIBLE
                    Glide.with(imgOfTheDay.context)
                        .load(jsonTxt.getString("url"))
                        .into(imgOfTheDay)
                    stopFetching()
                }else{
                    video.visibility = View.VISIBLE
                    iotd.visibility = View.INVISIBLE
                    var uri = Uri.parse(jsonTxt.getString("url"))
                    video.setVideoURI(uri)
                    video.start()
                }
            }
        })



    }

    private fun stopFetching() {

    }

    private fun startFetching() {
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        var sdf = SimpleDateFormat("yyyy-MM-dd")
        var dateQuery = sdf.format(calendar.timeInMillis)
        getDateImage(dateQuery)
    }

}