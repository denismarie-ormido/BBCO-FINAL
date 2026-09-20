package com.example.kayara

data class FeedPostModel(
    val postType: String,        // "text" o "photo"
    val posterName: String,
    val posterRole: String,      // "Super Admin" o "Admin"
    val timeAgo: String,
    val avatarInitials: String = "",
    val avatarColorStart: String = "#0B2E6B",
    val avatarColorEnd: String = "#1E5FD9",
    val audience: String = "everyone",  // "everyone" o "admins_only" (text posts ra)
    val textContent: String = "",       // para sa text posts
    val caption: String = "",           // para sa photo posts
    val imageUrls: List<String> = emptyList(), // 1 o 2 ka images
    val likeCount: Int = 0,
    val commentCount: Int = 0
)