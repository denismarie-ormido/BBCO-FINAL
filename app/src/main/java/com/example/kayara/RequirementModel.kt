package com.example.kayara

data class RequirementModel(
    val fileName: String,
    val uploadDate: String,
    val fileSize: String,
    val status: String,       // "approved", "pending", "rejected"
    val fileUrl: String = "", // Actual Cloudinary file URL

    // organizationDocuments/{docId} (para sa delete). Blangko kung gikan sa application/renewal.
    val docId: String = ""
)