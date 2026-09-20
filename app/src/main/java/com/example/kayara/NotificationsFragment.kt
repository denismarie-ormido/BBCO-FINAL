package com.example.kayara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        val toggleNotifPosts = view.findViewById<TextView>(R.id.toggleNotifPosts)
        val toggleNotifDocuments = view.findViewById<TextView>(R.id.toggleNotifDocuments)
        val rvNotifPosts = view.findViewById<RecyclerView>(R.id.rvNotifPosts)
        val rvNotifDocuments = view.findViewById<RecyclerView>(R.id.rvNotifDocuments)

        // TODO: I-replace ni sa Firestore query nga naay type == "post" o "document"
        val postNotifications = listOf(
            NotificationModel(
                "IT Society posted new photos",
                "\"Wrapped up our 3-day Intro to Web Dev Bootcamp...\"",
                "5 hours ago", "signup"
            ),
            NotificationModel(
                "Red Cross Youth posted new photos",
                "\"Blood Donation Drive 2026 — a huge thank you...\"",
                "2 hours ago", "signup"
            ),
            NotificationModel(
                "Supreme Student Government posted new photos",
                "\"Successful Freshmen Orientation Day...\"",
                "Yesterday", "signup", isRead = true
            )
        )

        val documentNotifications = listOf(
            NotificationModel(
                "IT Society submitted a document",
                "ITS_Constitution_Bylaws.docx — awaiting your review",
                "1 hour ago", "document"
            ),
            NotificationModel(
                "College Student Council submitted a document",
                "CSC_Financial_Statement_Q2.pdf — awaiting your review",
                "3 hours ago", "document"
            ),
            NotificationModel(
                "Red Cross Youth's document was approved",
                "RCY_Accomplishment_Report.pdf",
                "Yesterday", "document", isRead = true
            )
        )

        rvNotifPosts.layoutManager = LinearLayoutManager(requireContext())
        rvNotifPosts.adapter = NotificationAdapter(postNotifications)

        rvNotifDocuments.layoutManager = LinearLayoutManager(requireContext())
        rvNotifDocuments.adapter = NotificationAdapter(documentNotifications)

        toggleNotifPosts.setOnClickListener {
            toggleNotifPosts.setBackgroundResource(R.drawable.bg_toggle_active)
            toggleNotifPosts.setTextColor(resources.getColor(R.color.white, null))
            toggleNotifDocuments.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            toggleNotifDocuments.setTextColor(resources.getColor(R.color.text_gray, null))
            rvNotifPosts.visibility = View.VISIBLE
            rvNotifDocuments.visibility = View.GONE
        }

        toggleNotifDocuments.setOnClickListener {
            toggleNotifDocuments.setBackgroundResource(R.drawable.bg_toggle_active)
            toggleNotifDocuments.setTextColor(resources.getColor(R.color.white, null))
            toggleNotifPosts.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            toggleNotifPosts.setTextColor(resources.getColor(R.color.text_gray, null))
            rvNotifDocuments.visibility = View.VISIBLE
            rvNotifPosts.visibility = View.GONE
        }

        return view
    }
}