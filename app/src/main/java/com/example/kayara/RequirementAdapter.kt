package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RequirementAdapter(
    private val requirements: MutableList<RequirementModel>
) : RecyclerView.Adapter<RequirementAdapter.ReqViewHolder>() {

    inner class ReqViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvDocName)
        val tvMeta: TextView = itemView.findViewById(R.id.tvDocMeta)
        val tvStatus: TextView = itemView.findViewById(R.id.tvDocStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_requirement_doc, parent, false)
        return ReqViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReqViewHolder, position: Int) {
        val doc = requirements[position]
        holder.tvName.text = doc.fileName
        holder.tvMeta.text = "Uploaded ${doc.uploadDate} · ${doc.fileSize}"

        when (doc.status) {
            "approved" -> {
                holder.tvStatus.text = "APPROVED"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_active)
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.teal_deep))
            }
            "pending" -> {
                holder.tvStatus.text = "UNDER REVIEW"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_pending)
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.amber_text))
            }
            else -> {
                holder.tvStatus.text = "REJECTED"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_overdue)
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.coral_text))
            }
        }
    }

    override fun getItemCount(): Int = requirements.size

    fun addDocument(doc: RequirementModel) {
        requirements.add(0, doc)
        notifyItemInserted(0)
    }
}