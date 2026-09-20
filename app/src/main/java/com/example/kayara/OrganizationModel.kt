package com.example.kayara

data class OrganizationModel(
    val orgName: String,
    val shortCode: String,
    val memberCount: Int,
    val adviserName: String,
    val category: String,   // "joined", "pending", "available"
    val colorStart: String = "#1E5FD9",
    val colorEnd: String = "#3FA9F5",
    val recruitmentInfo: String = ""
)