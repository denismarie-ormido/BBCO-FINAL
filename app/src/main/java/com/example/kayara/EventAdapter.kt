package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val events: MutableList<EventModel>,
    private val onLongClick: (EventModel) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView =
            itemView.findViewById(R.id.tvEventTitle)

        val tvDateTime: TextView =
            itemView.findViewById(R.id.tvEventDateTime)

        val tvVenue: TextView =
            itemView.findViewById(R.id.tvEventVenue)

        val tvDescription: TextView =
            itemView.findViewById(R.id.tvEventDescription)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_event,
                    parent,
                    false
                )

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: EventViewHolder,
        position: Int
    ) {

        val event =
            events[position]

        holder.tvTitle.text =
            event.title

        holder.tvDateTime.text =
            "${event.date} · ${event.time}"

        holder.tvVenue.text =
            if (event.venue.isBlank()) {
                "No venue specified"
            } else {
                event.venue
            }

        holder.tvDescription.text =
            if (event.description.isBlank()) {
                "No description."
            } else {
                event.description
            }

        holder.itemView.setOnLongClickListener {

            onLongClick(event)

            true
        }
    }

    override fun getItemCount(): Int =
        events.size

    fun updateEvents(
        newEvents: List<EventModel>
    ) {

        events.clear()

        events.addAll(newEvents)

        notifyDataSetChanged()
    }
}