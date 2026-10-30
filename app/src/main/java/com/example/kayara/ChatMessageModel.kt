package com.example.kayara

data class ChatMessageModel(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",

    // "Admin", officer position (pananglitan "President"), o blangko kung regular member
    val senderRole: String = "",

    val senderPhotoUrl: String = "",
    val text: String = "",
    val timeMillis: Long = 0L
)