package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DocumentFolderAdapter(
    private val folders: List<DocumentFolderModel>,
    private val onFolderClick: (DocumentFolderModel) -> Unit
) : RecyclerView.Adapter<DocumentFolderAdapter.FolderRowViewHolder>() {

    inner class FolderRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvFolderRowName)
        val tvCount: TextView = itemView.findViewById(R.id.tvFolderRowCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderRowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder_row, parent, false)
        return FolderRowViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderRowViewHolder, position: Int) {
        val folder = folders[position]
        holder.tvName.text = folder.folderName
        holder.tvCount.text = "${folder.fileCount} files"
        holder.itemView.setOnClickListener { onFolderClick(folder) }
    }

    override fun getItemCount(): Int = folders.size
}