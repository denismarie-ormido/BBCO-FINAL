package com.example.kayara

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DocumentsFragment : Fragment() {

    private lateinit var folderAdapter: FolderAdapter
    private lateinit var tvTotalFolders: TextView
    private lateinit var tvTotalPending: TextView

    // Bag-ong: class-level mutable list
    private val folders = mutableListOf(
        FolderModel("Red Cross Youth", 4, "clear", folderColor = "#12B5A6"),
        FolderModel("IT Society", 6, "pending", pendingCount = 1, folderColor = "#F5A524"),
        FolderModel("Supreme Student Gov't", 9, "clear", folderColor = "#0B2E6B"),
        FolderModel("College Student Council", 3, "overdue", pendingCount = 1, folderColor = "#EF5350"),
        FolderModel("Educators' Society", 5, "clear", folderColor = "#2E9DF7"),
        FolderModel("BS Agri-Forestry Society", 2, "clear", folderColor = "#17C2AE")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_documents, container, false)

        tvTotalFolders = view.findViewById(R.id.tvTotalFolders)
        tvTotalPending = view.findViewById(R.id.tvTotalPending)
        val rvFolders = view.findViewById<RecyclerView>(R.id.rvFolders)
        val fabAddFolder = view.findViewById<ImageView>(R.id.fabAddFolder)

        updateSummary()

        rvFolders.layoutManager = GridLayoutManager(requireContext(), 2)
        folderAdapter = FolderAdapter(folders) { position ->
            val folder = folders[position]
            Toast.makeText(requireContext(), "Opening ${folder.orgName}'s folder...", Toast.LENGTH_SHORT).show()
            // TODO: mo-adto sa OrgDocumentsActivity nga naay listahan sa individual files
        }
        rvFolders.adapter = folderAdapter

        // Bag-ong: mag-open og dialog pag-klik sa FAB
        fabAddFolder.setOnClickListener {
            showAddFolderDialog()
        }

        return view
    }

    private fun showAddFolderDialog() {
        val input = EditText(requireContext())
        input.hint = "Organization name"
        input.setPadding(40, 30, 40, 30)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Organization Folder")
            .setMessage("Enter the name of the organization")
            .setView(input)
            .setPositiveButton("Add") { dialog, _ ->
                val orgName = input.text.toString().trim()
                if (orgName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter an organization name", Toast.LENGTH_SHORT).show()
                } else {
                    val newFolder = FolderModel(
                        orgName = orgName,
                        fileCount = 0,
                        status = "clear",
                        folderColor = "#1E5FD9"
                    )
                    folders.add(0, newFolder)
                    folderAdapter.notifyItemInserted(0)
                    updateSummary()

                    // TODO: I-save pud sa Firestore diri (create collection/document sa organizations)

                    Toast.makeText(requireContext(), "$orgName folder created!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateSummary() {
        val totalPending = folders.sumOf { it.pendingCount }
        tvTotalFolders.text = "${folders.size} Organization Folders"
        tvTotalPending.text = "$totalPending documents awaiting review"
    }
}