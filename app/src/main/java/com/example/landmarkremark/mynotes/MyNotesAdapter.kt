package com.example.landmarkremark.mynotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.landmarkremark.R
import com.example.landmarkremark.model.Notes

class MyNotesAdapter(
    private val itemList: List<Notes>?,
    private val listener: OnMyNoteListener
) : RecyclerView.Adapter<MyNotesAdapter.MyViewHolder>() {

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location: TextView = itemView.findViewById(R.id.my_note_location_txt)
        val title: TextView = itemView.findViewById(R.id.title_txt)
        val des: TextView = itemView.findViewById(R.id.des_txt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_note_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val lat = itemList?.get(position)?.currentLocation!![0]
        val lng = itemList[position].currentLocation!![1]
        holder.location.text = "Lat: $lat - Lng: $lng"
        holder.title.text = "Title : " + itemList[position].title
        holder.des.text = "Description : " + itemList[position].description

        holder.itemView.setOnClickListener{
            listener.onNoteItemClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }
}