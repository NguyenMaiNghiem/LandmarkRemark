package com.example.landmarkremark.allnote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.landmarkremark.R
import com.example.landmarkremark.model.Notes

class NotesAdapter(
    private val itemList: MutableList<Notes>
) : RecyclerView.Adapter<NotesAdapter.MyViewHolder>() {

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.note_name_txt)
        val email: TextView = itemView.findViewById(R.id.note_mail_txt)
        val title: TextView = itemView.findViewById(R.id.note_title_txt)
        val des: TextView = itemView.findViewById(R.id.note_des_txt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = "Name : " + itemList[position].name
        holder.email.text = "Email : " + itemList[position].email
        holder.title.text = "Title : " + itemList[position].title
        holder.des.text = "Description : " + itemList[position].description
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}