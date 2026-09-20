package com.example.kayara

data class RequirementModel(
    val fileName: String,
    val uploadDate: String,
    val fileSize: String,
    val status: String   // "approved", "pending", "rejected"
)