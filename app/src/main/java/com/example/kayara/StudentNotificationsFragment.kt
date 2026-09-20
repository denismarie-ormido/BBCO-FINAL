package com.example.kayara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentNotificationsFragment : Fragment() {

    private lateinit var rvNotifications: RecyclerView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val notifications = mutableListOf<NotificationModel>()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_student_notifications,
            container,
            false
        )

        rvNotifications = view.findViewById(R.id.rvStudentNotifications)

        rvNotifications.layoutManager = LinearLayoutManager(requireContext())

        notificationAdapter = NotificationAdapter(notifications)
        rvNotifications.adapter = notificationAdapter

        loadNotifications()

        return view
    }

    private fun loadNotifications() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                requireContext(),
                "You are not logged in.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val uid = currentUser.uid

        db.collection("notifications")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->

                notifications.clear()

                for (document in result.documents) {

                    val title = document.getString("title") ?: ""
                    val message = document.getString("message") ?: ""
                    val type = document.getString("type") ?: "info"
                    val isRead = document.getBoolean("read") ?: false

                    val createdAt = document.getTimestamp("createdAt")

                    val timeAgo = if (createdAt != null) {
                        getTimeAgo(createdAt.toDate())
                    } else {
                        ""
                    }

                    val notification = NotificationModel(
                        notificationId = document.id,
                        title = title,
                        message = message,
                        timeAgo = timeAgo,
                        type = type,
                        isRead = isRead
                    )

                    notifications.add(notification)
                }

                notificationAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    requireContext(),
                    "Failed to load notifications: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun getTimeAgo(date: Date): String {

        val now = System.currentTimeMillis()
        val time = date.time

        val difference = now - time

        val seconds = difference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Just now"

            minutes < 60 -> {
                if (minutes == 1L) {
                    "1 minute ago"
                } else {
                    "$minutes minutes ago"
                }
            }

            hours < 24 -> {
                if (hours == 1L) {
                    "1 hour ago"
                } else {
                    "$hours hours ago"
                }
            }

            days < 7 -> {
                if (days == 1L) {
                    "Yesterday"
                } else {
                    "$days days ago"
                }
            }

            else -> {
                SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                ).format(date)
            }
        }
    }
}