package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Mga folder sa sulod sa usa ka folder (card style, parehas sa document cards).
 */
class SubfolderAdapter(
    private val folders: List<DocumentFolderModel>,
    private val onClick: (DocumentFolderModel) -> Unit,
    private val onLongClick: ((DocumentFolderModel) -> Unit)? = null
) : RecyclerView.Adapter<SubfolderAdapter.SubfolderViewHolder>() {

    inner class SubfolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvSubfolderName)
        val tvCount: TextView = itemView.findViewById(R.id.tvSubfolderCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubfolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subfolder, parent, false)
        return SubfolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubfolderViewHolder, position: Int) {

        val folder = folders[position]

        holder.tvName.text = folder.folderName

        holder.tvCount.text =
            if (folder.fileCount == 1) "1 item" else "${folder.fileCount} items"

        holder.itemView.setOnClickListener { onClick(folder) }

        if (onLongClick != null) {
            holder.itemView.setOnLongClickListener {
                onLongClick.invoke(folder)
                true
            }
        } else {
            holder.itemView.setOnLongClickListener(null)
        }
    }

    override fun getItemCount(): Int = folders.size
}