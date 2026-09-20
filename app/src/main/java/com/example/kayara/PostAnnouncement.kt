package com.example.kayara

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PostAnnouncementActivity : AppCompatActivity() {

    private val photoUris = mutableListOf<Uri>()
    private val documentUris = mutableListOf<Uri>()
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var rvPhotos: RecyclerView
    private lateinit var documentChipContainer: LinearLayout

    private var isAllOrgsSelected = true

    // Modern multi-photo picker (Android Photo Picker, works on all API levels via backport)
    private val pickPhotosLauncher =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                photoUris.addAll(uris)
                photoAdapter.notifyDataSetChanged()
                rvPhotos.visibility = RecyclerView.VISIBLE
            }
        }

    private val pickDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                documentUris.add(uri)
                addDocumentChip(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_announcement)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val chipAllOrgs = findViewById<TextView>(R.id.chipAllOrgs)
        val chipSpecificOrg = findViewById<TextView>(R.id.chipSpecificOrg)
        val btnAddPhoto = findViewById<LinearLayout>(R.id.btnAddPhoto)
        val btnAddDocument = findViewById<LinearLayout>(R.id.btnAddDocument)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnPost = findViewById<LinearLayout>(R.id.btnPost)
        documentChipContainer = findViewById(R.id.documentChipContainer)
        rvPhotos = findViewById(R.id.rvPhotos)

        rvPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        photoAdapter = PhotoAdapter(photoUris) { position ->
            photoUris.removeAt(position)
            photoAdapter.notifyDataSetChanged()
            if (photoUris.isEmpty()) rvPhotos.visibility = RecyclerView.GONE
        }
        rvPhotos.adapter = photoAdapter

        btnBack.setOnClickListener { finish() }

        chipAllOrgs.setOnClickListener {
            isAllOrgsSelected = true
            chipAllOrgs.setBackgroundResource(R.drawable.bg_chip_select_blue)
            chipAllOrgs.setTextColor(resources.getColor(R.color.white, null))
            chipSpecificOrg.setBackgroundResource(R.drawable.bg_chip)
            chipSpecificOrg.setTextColor(resources.getColor(R.color.text_gray, null))
        }

        chipSpecificOrg.setOnClickListener {
            isAllOrgsSelected = false
            chipSpecificOrg.setBackgroundResource(R.drawable.bg_chip_select_blue)
            chipSpecificOrg.setTextColor(resources.getColor(R.color.white, null))
            chipAllOrgs.setBackgroundResource(R.drawable.bg_chip)
            chipAllOrgs.setTextColor(resources.getColor(R.color.text_gray, null))
            // TODO: mugawas ang org picker (dialog/spinner) diri sa umaabot
        }

        btnAddPhoto.setOnClickListener {
            pickPhotosLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        btnAddDocument.setOnClickListener {
            pickDocumentLauncher.launch("*/*")
        }

        btnPost.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val message = etMessage.text.toString().trim()

            if (title.isEmpty() || message.isEmpty()) {
                showError(tvError, "Please fill in the title and message")
                return@setOnClickListener
            }

            // TODO: I-upload ang photoUris ug documentUris sa server, unya i-save
            // ang announcement record (title, message, audience, attachments, timestamp)

            tvError.visibility = TextView.GONE
            Toast.makeText(this, "Announcement posted successfully!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun addDocumentChip(uri: Uri) {
        val chipView = LayoutInflater.from(this).inflate(R.layout.item_file_chip, documentChipContainer, false)
        val tvFileName = chipView.findViewById<TextView>(R.id.tvFileName)
        val tvFileSize = chipView.findViewById<TextView>(R.id.tvFileSize)
        val btnRemove = chipView.findViewById<ImageView>(R.id.btnRemoveFile)

        val (name, sizeBytes) = getFileInfo(uri)
        tvFileName.text = name
        tvFileSize.text = formatFileSize(sizeBytes)

        btnRemove.setOnClickListener {
            documentChipContainer.removeView(chipView)
            documentUris.remove(uri)
        }

        documentChipContainer.addView(chipView)
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

    private fun showError(tvError: TextView, message: String) {
        tvError.text = message
        tvError.visibility = TextView.VISIBLE
    }
}