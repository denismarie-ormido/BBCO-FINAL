package com.example.kayara

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FolderDocumentsActivity : AppCompatActivity() {

    private lateinit var requirementAdapter: RequirementAdapter
    private var folderName: String = ""

    // TODO: I-replace ni sa Firestore query
    // WHERE organizationId == myOrgId AND folder == folderName
    private val documents = mutableListOf<RequirementModel>()

    private val pickDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) handlePickedDocument(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder_documents)

        folderName = intent.getStringExtra("folderName") ?: "Documents"
        findViewById<TextView>(R.id.tvFolderTitle).text = folderName

        if (folderName == "Renewal") {
            documents.add(RequirementModel("RCY_Renewal_Application_2026.pdf", "Aug 20", "1.1 MB", "pending"))
        } else {
            documents.add(RequirementModel("RCY_Accomplishment_Report.pdf", "Jun 3", "1.2 MB", "approved"))
            documents.add(RequirementModel("RCY_Officers_Roster.docx", "Jun 5", "340 KB", "approved"))
        }

        val rvFolderDocuments = findViewById<RecyclerView>(R.id.rvFolderDocuments)
        rvFolderDocuments.layoutManager = LinearLayoutManager(this)
        requirementAdapter = RequirementAdapter(documents)
        rvFolderDocuments.adapter = requirementAdapter

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<ImageView>(R.id.fabAddDocument).setOnClickListener {
            pickDocumentLauncher.launch("*/*")
        }
    }

    private fun handlePickedDocument(uri: Uri) {
        val (fileName, sizeBytes) = getFileInfo(uri)
        val newDoc = RequirementModel(
            fileName = fileName,
            uploadDate = "Just now",
            fileSize = formatFileSize(sizeBytes),
            status = "pending"
        )
        requirementAdapter.addDocument(newDoc)

        Toast.makeText(this, "$fileName uploaded to $folderName!", Toast.LENGTH_SHORT).show()
    }

    private fun getFileInfo(uri: Uri): Pair<String, Long> {
        var name = "Document"
        var size = 0L
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex != -1) name = it.getString(nameIndex)
                if (sizeIndex != -1) size = it.getLong(sizeIndex)
            }
        }
        return Pair(name, size)
    }

    private fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        return if (kb < 1024) "%.0f KB".format(kb) else "%.1f MB".format(kb / 1024.0)
    }
}