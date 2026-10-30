package com.example.kayara

data class NotificationModel(

    val notificationId: String = "",

    val title: String = "",

    val message: String = "",

    val timeAgo: String = "",

    val type: String = "info",

    val isRead: Boolean = false,

    // Organization nga tag-iya sa notification
    val organizationId: String = "",

    val organizationName: String = "",

    // General reference ID
    // mahimong membershipId, renewalId, postId, etc.
    val referenceId: String = "",

    // Optional specific references
    val studentId: String = "",

    val postId: String = ""
)