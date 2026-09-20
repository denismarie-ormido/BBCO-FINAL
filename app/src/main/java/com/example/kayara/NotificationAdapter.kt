package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private val notifications: List<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.NotifViewHolder>() {

    inner class NotifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgNotifIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvNotifTitle)
        val tvMessage: TextView = itemView.findViewById(R.id.tvNotifMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvNotifTime)
        val dotUnread: View = itemView.findViewById(R.id.dotUnread)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotifViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        val notif = notifications[position]
        holder.tvTitle.text = notif.title
        holder.tvMessage.text = notif.message
        holder.tvTime.text = notif.timeAgo
        holder.dotUnread.visibility = if (notif.isRead) View.GONE else View.VISIBLE

        when (notif.type) {
            "signup" -> holder.imgIcon.setImageResource(R.drawable.ic_person_add)
            "document" -> holder.imgIcon.setImageResource(R.drawable.ic_nav_documents)
            else -> holder.imgIcon.setImageResource(R.drawable.ic_bell)
        }
    }

    override fun getItemCount(): Int = notifications.size
}