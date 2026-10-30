package com.example.kayara

data class DocumentFolderModel(
    val folderName: String,
    val fileCount: Int = 0,

    // Wala = system/legacy folder (Organization Requirements, Documents 2026-2027, ...).
    // Naay value = folder nga gihimo sa admin (organizationFolders/{folderId}).
    val folderId: String = ""
)