package com.example.kayara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminNotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_admin_notifications,
            container,
            false
        )

        val notifications = listOf(

            NotificationModel(
                "Document Approved",
                "Your submitted RCY_Accomplishment_Report.pdf was approved by the Super Admin",
                "1 hour ago",
                "document"
            ),

            NotificationModel(
                "New Membership Request",
                "Angela Torres wants to join Red Cross Youth",
                "2 hours ago",
                "signup"
            ),

            NotificationModel(
                "New Announcement from Super Admin",
                "\"Reminder: General Assembly on September 5, 1:00 PM...\"",
                "2 hours ago",
                "info"
            ),

            NotificationModel(
                "New Membership Request",
                "Jomar Ramos wants to join Red Cross Youth",
                "3 hours ago",
                "signup",
                isRead = true
            ),

            NotificationModel(
                "Renewal Reminder",
                "Your organization's accreditation is due for renewal soon",
                "Yesterday",
                "document",
                isRead = true
            ),

            NotificationModel(
                "Application Approved",
                "Your organization Red Cross Youth has been approved by the Super Admin",
                "2 days ago",
                "signup",
                isRead = true
            )
        )

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.rvAdminNotifications)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerView.adapter =
            NotificationAdapter(notifications)

        return view
    }
}