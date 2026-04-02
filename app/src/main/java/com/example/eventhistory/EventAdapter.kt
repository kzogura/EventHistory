package com.example.eventhistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 履歴リストを表示するためのアダプターです。
 */
class EventAdapter(private val events: MutableList<EventEntry>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(android.R.id.text1)
        val dateTextView: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.nameTextView.text = "[${event.category}] ${event.itemName}"
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        holder.dateTextView.text = "${dateFormat.format(event.actionDate)} - ${event.memo}"
        
        holder.itemView.contentDescription = "履歴アイテム: ${event.itemName}"
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<EventEntry>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
}
