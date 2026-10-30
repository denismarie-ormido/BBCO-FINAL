package com.example.kayara

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.widget.TextView
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

// =========================================================
// RECRUITMENT MODELS
//
// organizations/{id}
//   recruitmentInfo          : String   (instructions para sa applicants)
//   recruitmentFiles         : [ { name, url } ]                  (forms/flyers nga i-download sa student)
//   recruitmentRequirements  : [ { id, title, required } ]        (unsay i-upload sa student)
//
// memberships/{uid_orgId}
//   submittedDocuments       : [ { requirementId, title, fileName, url } ]
// =========================================================

// Klase sa files nga pwede pilion sa file picker
val DOCUMENT_MIME_TYPES = arrayOf(
    "application/pdf",
    "image/*",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "application/vnd.ms-powerpoint",
    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
    "application/vnd.ms-excel",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "text/plain"
)

data class RecruitmentFile(
    val name: String,
    val url: String
)

data class RecruitmentRequirement(
    val id: String,
    val title: String,
    val required: Boolean = true
)

data class SubmittedDocument(
    val requirementId: String,
    val title: String,
    val fileName: String,
    val url: String
)

// =========================================================
// PARSERS (Firestore -> models)
// =========================================================

fun parseRecruitmentFiles(raw: Any?): List<RecruitmentFile> {

    return (raw as? List<*>)
        ?.mapNotNull { item ->

            val map = item as? Map<*, *>

            val url = map?.get("url")?.toString().orEmpty()

            if (map == null || url.isBlank()) {
                null
            } else {
                RecruitmentFile(
                    name = map["name"]?.toString()
                        ?.takeIf { it.isNotBlank() }
                        ?: "File",
                    url = url
                )
            }
        }
        ?: emptyList()
}

fun parseRecruitmentRequirements(raw: Any?): List<RecruitmentRequirement> {

    return (raw as? List<*>)
        ?.mapNotNull { item ->

            val map = item as? Map<*, *>

            val id = map?.get("id")?.toString().orEmpty()
            val title = map?.get("title")?.toString().orEmpty()

            if (map == null || id.isBlank() || title.isBlank()) {
                null
            } else {
                RecruitmentRequirement(
                    id = id,
                    title = title,
                    required = map["required"] as? Boolean ?: true
                )
            }
        }
        ?: emptyList()
}

fun parseSubmittedDocuments(raw: Any?): List<SubmittedDocument> {

    return (raw as? List<*>)
        ?.mapNotNull { item ->

            val map = item as? Map<*, *>

            val url = map?.get("url")?.toString().orEmpty()

            if (map == null || url.isBlank()) {
                null
            } else {
                SubmittedDocument(
                    requirementId = map["requirementId"]?.toString().orEmpty(),
                    title = map["title"]?.toString()
                        ?.takeIf { it.isNotBlank() }
                        ?: "Document",
                    fileName = map["fileName"]?.toString().orEmpty(),
                    url = url
                )
            }
        }
        ?: emptyList()
}

// =========================================================
// UI HELPERS
// =========================================================

// REQUIRED (blue) / OPTIONAL (gray) tag
fun styleRequirementTag(
    textView: TextView,
    required: Boolean
) {

    val context = textView.context

    if (required) {

        textView.text = "REQUIRED"

        textView.setBackgroundResource(
            R.drawable.bg_role_officer
        )

        textView.setTextColor(
            context.getColor(R.color.blue_deep)
        )

    } else {

        textView.text = "OPTIONAL"

        textView.setBackgroundResource(
            R.drawable.bg_role_member
        )

        textView.setTextColor(
            context.getColor(R.color.text_gray)
        )
    }
}

// Abli ang file/link sa browser o sa viewer app
fun openUrl(
    context: Context,
    url: String
) {

    try {

        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )

    } catch (e: Exception) {

        Toast.makeText(
            context,
            "No app found to open this file.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

// Ngan sa file gikan sa Uri
fun getFileName(
    context: Context,
    uri: Uri
): String {

    var name = "file"

    try {

        context.contentResolver
            .query(uri, null, null, null, null)
            ?.use { cursor ->

                val index =
                    cursor.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME
                    )

                if (index >= 0 && cursor.moveToFirst()) {
                    name = cursor.getString(index) ?: name
                }
            }

    } catch (e: Exception) {
        // gamiton ang default nga ngan
    }

    return name
}

// =========================================================
// CLOUDINARY UPLOAD (images, PDF, Word, etc.)
// Ang callbacks kanunay mo-agi sa main thread.
// =========================================================

fun uploadFileToCloudinary(
    uri: Uri,
    folder: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {

    val mainHandler = Handler(Looper.getMainLooper())

    try {

        MediaManager.get()
            .upload(uri)
            .unsigned("kayara_upload")
            .option("folder", folder)
            .option("resource_type", "auto")
            .callback(
                object : UploadCallback {

                    override fun onStart(requestId: String?) {}

                    override fun onProgress(
                        requestId: String?,
                        bytes: Long,
                        totalBytes: Long
                    ) {
                    }

                    override fun onSuccess(
                        requestId: String?,
                        resultData: Map<*, *>?
                    ) {

                        val url =
                            resultData?.get("secure_url")?.toString()

                        mainHandler.post {

                            if (url.isNullOrBlank()) {
                                onError("Upload finished but no link was returned.")
                            } else {
                                onSuccess(url)
                            }
                        }
                    }

                    override fun onError(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {

                        mainHandler.post {
                            onError(error?.description ?: "Unknown error")
                        }
                    }

                    override fun onReschedule(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {
                    }
                }
            )
            .dispatch()

    } catch (e: Exception) {

        mainHandler.post {
            onError(e.message ?: "Upload failed")
        }
    }
}