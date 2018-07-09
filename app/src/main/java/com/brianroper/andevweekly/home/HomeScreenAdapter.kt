package com.brianroper.andevweekly.home

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brianroper.andevweekly.R
import com.brianroper.andevweekly.archive.ArchiveActivity
import kotlinx.android.synthetic.main.home_item.view.*

class HomeScreenAdapter(val items : ArrayList<HomeItem>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.home_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.itemTitle?.text = items.get(position).title
        holder?.itemImage.setImageResource(items.get(position).imageResource)
        holder?.itemLayout.setOnClickListener {
            if (position == 0){
                val intent = Intent(context, ArchiveActivity::class.java)
                startActivity(context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val itemTitle = view.home_item_title
    val itemImage = view.home_item_image
    val itemLayout = view.home_layout
}