package com.example.nasaimages

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.nasaimages.databinding.MainFragmentBinding
import kotlinx.android.synthetic.main.main_fragment.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    lateinit var binding: MainFragmentBinding

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    var YOUTUBE_VIDEO_ID = ""
    var isYoutubeVideo = false
    var videoUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var connMgr = binding.root.context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        var networkInfo = connMgr.activeNetworkInfo

        if (!(networkInfo != null && networkInfo.isConnected)) {
            binding.textView2.text = "No Internet Connection"
        }

        binding.setDate.setOnClickListener {
            var datePicker = DatePickerFragment()
            datePicker.setTargetFragment(this, 0)
            datePicker.show(this!!.fragmentManager!!, "Date Picker")
            //TODO: implement pagination
        }

        binding.videoOfTheDay.setOnClickListener {
            if (isYoutubeVideo) {
                playVideo(YOUTUBE_VIDEO_ID, (binding.root.context))
            } else {
                sendMediaIntent()
            }
        }

        viewModel.mediaDetails.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { content ->
                binding.textViewImg.text = content
            }
        )

        viewModel.isLoading.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { isLoading ->
                if (isLoading) {
                    binding.loading1.visibility = View.VISIBLE
                } else {
                    binding.loading1.visibility = View.GONE
                }
            }
        )

        viewModel.jsonTxt.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { stringObtained ->
                var jsonTxt = JSONObject(stringObtained)
                if (jsonTxt.getString("media_type") == "image") {
                    binding.textView2.text = "IMAGE OF THE DAY"
                    binding.videoOfTheDay.visibility = View.GONE
                    binding.imgOfTheDay.visibility = View.VISIBLE
                    Glide.with(imgOfTheDay.context)
                        .load(jsonTxt.getString("url"))
                        .placeholder(R.drawable.ic_moon)
                        .into(imgOfTheDay)
                } else if (jsonTxt.getString("url")
                        .substring(
                            0,
                            "https://www.youtube.com/embed/".length
                        ) == "https://www.youtube.com/embed/"
                ) {
                    binding.textView2.text = "VIDEO OF THE DAY"
                    binding.videoOfTheDay.visibility = View.VISIBLE
                    binding.imgOfTheDay.visibility = View.GONE
                    var url = jsonTxt.getString("url")
                    YOUTUBE_VIDEO_ID =
                        url.substring("https://www.youtube.com/embed/".length, url.length - 6)
                    playVideo(YOUTUBE_VIDEO_ID, (binding.root.context))
                    isYoutubeVideo = true
                } else {
                    isYoutubeVideo = false
                    videoUrl = jsonTxt.getString("url")
                    sendMediaIntent()
                }

            })

        viewModel.failureText.observe(viewLifecycleOwner, androidx.lifecycle.Observer { msg ->
            binding.textViewImg.text = msg
        }
        )

        return binding.root
    }

    private fun sendMediaIntent() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(videoUrl)
        startActivity(i)
    }

    private fun playVideo(youtubeVideoId: String, mcontext: Context) {
        var i = Intent(mcontext, YoutubePlayerActivity::class.java)
        i.putExtra("YOUTUBE_VIDEO_ID", youtubeVideoId)
        startActivity(i)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        if (calendar.timeInMillis <= Calendar.getInstance().timeInMillis) {
            var sdf = SimpleDateFormat("yyyy-MM-dd")
            var dateQuery = sdf.format(calendar.timeInMillis)
            viewModel.getDateImage(dateQuery)
        } else {
            Toast.makeText(context, "Invalid Response", Toast.LENGTH_SHORT).show()
        }
    }


}