package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(
    private val folders: List<FolderModel>,
    private val onFolderClick: (Int) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgFolderIcon)
        val tvOrgName: TextView = itemView.findViewById(R.id.tvFolderOrgName)
        val tvMeta: TextView = itemView.findViewById(R.id.tvFolderMeta)
        val tvBadge: TextView = itemView.findViewById(R.id.tvFolderBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.tvOrgName.text = folder.orgName
        holder.tvMeta.text = "${folder.fileCount} files"
        holder.imgIcon.setColorFilter(android.graphics.Color.parseColor(folder.folderColor))

        when (folder.status) {
            "pending" -> {
                holder.tvBadge.text = "${folder.pendingCount} PENDING"
                holder.tvBadge.setBackgroundResource(R.drawable.bg_folder_badge_attn)
                holder.tvBadge.setTextColor(holder.itemView.context.getColor(R.color.amber_text))
            }
            "overdue" -> {
                holder.tvBadge.text = "${folder.pendingCount} OVERDUE"
                holder.tvBadge.setBackgroundResource(R.drawable.bg_folder_badge_attn)
                holder.tvBadge.setTextColor(holder.itemView.context.getColor(R.color.coral_text))
            }
            else -> {
                holder.tvBadge.text = "CLEAR"
                holder.tvBadge.setBackgroundResource(R.drawable.bg_folder_badge_clear)
                holder.tvBadge.setTextColor(holder.itemView.context.getColor(R.color.teal_deep))
            }
        }

        holder.itemView.setOnClickListener { onFolderClick(holder.adapterPosition) }
    }

    override fun getItemCount(): Int = folders.size
}