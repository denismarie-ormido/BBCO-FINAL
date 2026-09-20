package com.example.kayara

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RequirementsFragment : Fragment() {

    private lateinit var folderAdapter: DocumentFolderAdapter
    private lateinit var rvDocumentFolders: RecyclerView

    // TODO: I-replace ni sa Firestore query
    // WHERE organizationId == myOrgId, GROUP BY folder
    private val folders = mutableListOf(
        DocumentFolderModel("Renewal", 1),
        DocumentFolderModel("Documents 2025-2026", 3),
        DocumentFolderModel("Documents 2024-2025", 5),
        DocumentFolderModel("Documents 2023-2024", 4)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_requirements, container, false)

        rvDocumentFolders = view.findViewById(R.id.rvDocumentFolders)
        rvDocumentFolders.layoutManager = LinearLayoutManager(requireContext())

        folderAdapter = DocumentFolderAdapter(folders) { folder ->
            val intent = Intent(requireContext(), FolderDocumentsActivity::class.java)
            intent.putExtra("folderName", folder.folderName)
            startActivity(intent)
        }
        rvDocumentFolders.adapter = folderAdapter

        val fabAddFolder = view.findViewById<ImageView>(R.id.fabAddFolder)
        fabAddFolder.setOnClickListener {
            showAddFolderDialog()
        }

        return view
    }

    private fun showAddFolderDialog() {
        val input = EditText(requireContext())
        input.hint = "e.g. Documents 2026-2027"
        input.setPadding(40, 30, 40, 30)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Folder")
            .setMessage("Enter a name for the new folder")
            .setView(input)
            .setPositiveButton("Add") { dialog, _ ->
                val name = input.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a folder name", Toast.LENGTH_SHORT).show()
                } else {
                    folders.add(DocumentFolderModel(name, 0))
                    folderAdapter.notifyItemInserted(folders.size - 1)
                    // TODO: I-save pud sa Firestore (organizations/{myOrgId}/folders)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}