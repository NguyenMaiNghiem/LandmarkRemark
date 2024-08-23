package com.example.landmarkremark.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.landmarkremark.R
import com.example.landmarkremark.model.Notes

class SearchNotesAdapter(
    private val itemList: List<Notes>,
    private val listener: OnSearchNoteListener
) : RecyclerView.Adapter<SearchNotesAdapter.MyViewHolder>() {

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location: TextView = itemView.findViewById(R.id.search_location_txt)
        val name: TextView = itemView.findViewById(R.id.search_note_name_txt)
        val email: TextView = itemView.findViewById(R.id.search_note_mail_txt)
        val title: TextView = itemView.findViewById(R.id.search_note_title_txt)
        val des: TextView = itemView.findViewById(R.id.search_note_des_txt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_note_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val lat = itemList[position].currentLocation!![0]
        val lng = itemList[position].currentLocation!![1]
        holder.location.text = "Lat: $lat - Lng: $lng"
        holder.name.text = "Name : " + itemList[position].name
        holder.email.text = "Email : " + itemList[position].email
        holder.title.text = "Title : " + itemList[position].title
        holder.des.text = "Description : " + itemList[position].description

        holder.itemView.setOnClickListener{
            listener.onSearchNoteItemClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}