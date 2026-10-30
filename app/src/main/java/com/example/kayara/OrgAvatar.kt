package com.example.kayara

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * Nagabantay sa tanang organizations ug nagahatag ug map:
 * organizationId -> avatarUrl (profile picture sa org).
 *
 * Gamiton sa tanang feed (Admin, Super Admin, Student, Org Profile).
 * Ayaw kalimot i-remove ang ListenerRegistration sa onDestroyView.
 */
fun listenToOrgAvatars(
    onChange: (Map<String, String>) -> Unit
): ListenerRegistration {

    return FirebaseFirestore.getInstance()
        .collection("organizations")
        .addSnapshotListener { snapshot, error ->

            if (error != null || snapshot == null) {
                return@addSnapshotListener
            }

            val avatars = mutableMapOf<String, String>()

            snapshot.documents.forEach { document ->

                val url = document.getString("avatarUrl")

                if (!url.isNullOrBlank()) {
                    avatars[document.id] = url
                }
            }

            onChange(avatars)
        }
}