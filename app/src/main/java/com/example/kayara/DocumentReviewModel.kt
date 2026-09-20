package com.example.kayara

data class DocumentReviewModel(
    val fileName: String,
    val orgName: String,
    val dateSubmitted: String,
    val preview: String,
    var status: String = "pending" // pending, approved, rejected
)