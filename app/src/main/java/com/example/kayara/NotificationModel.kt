package com.example.kayara

data class NotificationModel(
    val notificationId: String = "",
    val title: String = "",
    val message: String = "",
    val timeAgo: String = "",
    val type: String = "info",
    val isRead: Boolean = false
)