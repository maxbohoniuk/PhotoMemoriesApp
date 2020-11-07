package com.example.prm02

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.prm02.db.Note
import kotlinx.android.synthetic.main.item_layout.view.*

class ItemViewHolder(view: View,activity: MainActivity) : RecyclerView.ViewHolder(view) {

    init {
        itemView.watchBtn.setOnClickListener {
            activity.watch_clicked(adapterPosition)




        }


    }

    fun setInfo(note: Note){
        itemView.titleText.text = note.title
        itemView.dateText.text = note.date
        if(!note.photo_path.isEmpty()) {
            itemView.photoImageView.setImageBitmap(BitmapFactory.decodeFile(note.photo_path))
        }


    }








}


class MyAdapter(activity: MainActivity) : RecyclerView.Adapter<ItemViewHolder>() {
    var activity: MainActivity = activity

    var notes = listOf<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            .let { ItemViewHolder(it,activity) }


    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setInfo(notes.get(position))

    }

    fun refresh(newList: List<Note>){
        this.notes = newList
        notifyDataSetChanged()

    }




}