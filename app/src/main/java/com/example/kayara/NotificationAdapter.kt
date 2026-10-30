package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private val notifications: List<NotificationModel>,

    private val onNotificationClick:
    ((NotificationModel) -> Unit)? = null,

    private val onNotificationLongClick:
    ((NotificationModel) -> Unit)? = null

) : RecyclerView.Adapter<NotificationAdapter.NotifViewHolder>() {

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    inner class NotifViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val imgIcon: ImageView =
            itemView.findViewById(
                R.id.imgNotifIcon
            )

        val tvTitle: TextView =
            itemView.findViewById(
                R.id.tvNotifTitle
            )

        val tvMessage: TextView =
            itemView.findViewById(
                R.id.tvNotifMessage
            )

        val tvTime: TextView =
            itemView.findViewById(
                R.id.tvNotifTime
            )

        val dotUnread: View =
            itemView.findViewById(
                R.id.dotUnread
            )
    }

    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotifViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_notification,
                parent,
                false
            )

        return NotifViewHolder(
            view
        )
    }

    // =========================================================
    // BIND
    // =========================================================

    override fun onBindViewHolder(
        holder: NotifViewHolder,
        position: Int
    ) {

        val notification =
            notifications[position]

        holder.tvTitle.text =
            notification.title

        holder.tvMessage.text =
            notification.message

        holder.tvTime.text =
            notification.timeAgo

        // =====================================================
        // UNREAD
        // =====================================================

        holder.dotUnread.visibility =
            if (
                notification.isRead
            ) {
                View.GONE
            } else {
                View.VISIBLE
            }

        // =====================================================
        // ICON
        // =====================================================

        when (
            notification.type
                .trim()
                .lowercase()
        ) {

            "membership",
            "membership_application",
            "signup",
            "document",
            "resubmission",
            "chat",
            "renewal",
            "comment",
            "post",
            "announcement",
            "warning",
            "success" -> {

                holder.imgIcon.setImageResource(
                    R.drawable.ic_bell
                )
            }

            else -> {

                holder.imgIcon.setImageResource(
                    R.drawable.ic_bell
                )
            }
        }

        // =====================================================
        // CLICK
        // =====================================================

        holder.itemView.setOnClickListener {

            onNotificationClick?.invoke(
                notification
            )
        }

        // =====================================================
        // LONG CLICK: DELETE / CLEAR ALL
        // =====================================================

        holder.itemView.setOnLongClickListener {

            if (onNotificationLongClick != null) {

                onNotificationLongClick.invoke(
                    notification
                )

                true

            } else {

                false
            }
        }
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    override fun getItemCount(): Int =
        notifications.size
}
