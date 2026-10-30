package com.example.kayara

import com.google.firebase.Timestamp

data class AdminApplicationDocument(
    val key: String = "",
    val label: String = "",
    val url: String = "",
    val publicId: String = ""
)

data class AdminApplicationModel(
    val applicationId: String = "",
    val uid: String = "",
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val organizationName: String = "",
    val organizationDescription: String = "",
    val organizationId: String = "",

    // NEW: 3 uploaded documents
    val documents: List<AdminApplicationDocument> = emptyList(),

    // Kept for compatibility with older applications
    val documentUrl: String = "",
    val documentPublicId: String = "",

    val role: String = "admin",
    val status: String = "pending",
    val rejectionReason: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)