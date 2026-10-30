package com.example.kayara

data class FolderModel(
    val orgName: String = "",
    val fileCount: Int = 0,
    val status: String = "clear",
    val pendingCount: Int = 0,
    val folderColor: String = "#1E5FD9",

    // Firestore references
    val organizationId: String = "",
    val applicationId: String = ""
)