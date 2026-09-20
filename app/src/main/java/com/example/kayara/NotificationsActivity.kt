package com.example.kayara

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val notifications = listOf(
            NotificationModel(
                "New Admin Application",
                "Ma'am Reyes applied to register Peer Facilitators Society",
                "10 minutes ago", "signup"
            ),
            NotificationModel(
                "Document Submitted",
                "IT Society uploaded ITS_Constitution_Bylaws.docx for review",
                "1 hour ago", "document"
            ),
            NotificationModel(
                "New Admin Application",
                "Sir Dela Cruz applied to register BS Agri-Forestry Society",
                "3 hours ago", "signup"
            ),
            NotificationModel(
                "Document Submitted",
                "Red Cross Youth uploaded RCY_Accomplishment_Report.pdf",
                "Yesterday", "document", isRead = true
            )
        )

        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = NotificationAdapter(notifications)
    }
}