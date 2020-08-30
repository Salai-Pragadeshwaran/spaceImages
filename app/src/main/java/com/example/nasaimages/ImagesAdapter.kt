package com.example.nasaimages

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.image_item.view.*
import java.util.ArrayList

class ImagesAdapter(var images: ArrayList<ImageData>, var mContext: Context) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView
        internal var img: ImageView
        internal var container: ConstraintLayout

        init {
            title = itemView.nasaImageTitle
            img = itemView.nasaImage
            container = itemView.imageItemContainer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = images[position].title
        if (images[position].url!="") {
            Glide.with(mContext)
                .load(images[position].url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.img)
        }
        holder.container.setOnClickListener {
            var i = Intent(mContext, AssetActivity::class.java)
            i.putExtra("NASA_ID", images[position].nasa_id)
            mContext.startActivity(i)
        }
    }
}