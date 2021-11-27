package com.example.mynotes.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mynotes.entities.Notes
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import kotlinx.android.synthetic.main.item_rv_notes.view.*
import android.graphics.Color
import kotlin.collections.ArrayList

class NotesAdapter() :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    var listener:OnItemClickListener? = null
    var arrList = ArrayList<Notes>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_notes,parent,false)
        )
    }
    override fun getItemCount(): Int {
        return arrList.size
    }
    fun setData(arrNotesList: List<Notes>){
        arrList = arrNotesList as ArrayList<Notes>
    }
    fun setOnClickListener(listener1: OnItemClickListener){
        listener = listener1
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        holder.itemView.tvTitle.text = arrList[position].title
        holder.itemView.tvDesc.text = arrList[position].noteText
        holder.itemView.tvDateTime.text = arrList[position].dateTime

        if (arrList[position].color != null){
            holder.itemView.cardView.setCardBackgroundColor(Color.parseColor(arrList[position].color))
        }else{
            holder.itemView.cardView.setCardBackgroundColor(R.color.ColorLightBlack)
        }

        if (arrList[position].webLink != ""){
            holder.itemView.tvWebLink.text = arrList[position].webLink
            holder.itemView.tvWebLink.visibility = View.VISIBLE
        }else{
            holder.itemView.tvWebLink.visibility = View.GONE
        }

        holder.itemView.cardView.setOnClickListener {
            listener!!.onClicked(arrList[position].id!!)
        }

    }
    class NotesViewHolder(view:View) : RecyclerView.ViewHolder(view){
    }
    interface OnItemClickListener{
        fun onClicked(noteId:Int)
    }

}
