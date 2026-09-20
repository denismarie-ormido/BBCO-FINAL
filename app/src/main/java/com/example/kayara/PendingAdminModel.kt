package com.example.kayara

data class PendingAdminModel(
    val fullName: String,
    val orgName: String,
    val username: String,
    var status: String = "pending" // pending, approved, rejected
)