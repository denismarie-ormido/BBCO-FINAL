package com.example.kayara

data class FolderModel(
    val orgName: String,
    val fileCount: Int,
    val status: String,       // "clear", "pending", "overdue"
    val pendingCount: Int = 0,
    val folderColor: String = "#12B5A6"
)