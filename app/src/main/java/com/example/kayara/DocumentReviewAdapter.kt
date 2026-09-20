package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DocumentReviewAdapter(
    private val documents: MutableList<DocumentReviewModel>,
    private val onApprove: (Int) -> Unit,
    private val onReject: (Int) -> Unit
) : RecyclerView.Adapter<DocumentReviewAdapter.DocViewHolder>() {

    inner class DocViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tvDocFileName)
        val tvMeta: TextView = itemView.findViewById(R.id.tvDocMeta)
        val tvPreview: TextView = itemView.findViewById(R.id.tvDocPreview)
        val actionRow: LinearLayout = itemView.findViewById(R.id.docActionRow)
        val tvStatusResult: TextView = itemView.findViewById(R.id.tvDocStatusResult)
        val btnApprove: TextView = itemView.findViewById(R.id.btnApprove)
        val btnReject: TextView = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document_review, parent, false)
        return DocViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        val doc = documents[position]
        holder.tvFileName.text = doc.fileName
        holder.tvMeta.text = "${doc.orgName} · Submitted ${doc.dateSubmitted}"
        holder.tvPreview.text = doc.preview

        when (doc.status) {
            "approved" -> {
                holder.actionRow.visibility = View.GONE
                holder.tvStatusResult.visibility = View.VISIBLE
                holder.tvStatusResult.text = "✓ Approved"
                holder.tvStatusResult.setTextColor(holder.itemView.context.getColor(R.color.teal_deep))
                holder.tvStatusResult.setBackgroundResource(R.drawable.bg_tag_active)
            }
            "rejected" -> {
                holder.actionRow.visibility = View.GONE
                holder.tvStatusResult.visibility = View.VISIBLE
                holder.tvStatusResult.text = "✕ Rejected"
                holder.tvStatusResult.setTextColor(holder.itemView.context.getColor(R.color.coral_text))
                holder.tvStatusResult.setBackgroundResource(R.drawable.bg_tag_overdue)
            }
            else -> {
                holder.actionRow.visibility = View.VISIBLE
                holder.tvStatusResult.visibility = View.GONE
            }
        }

        holder.btnApprove.setOnClickListener { onApprove(holder.adapterPosition) }
        holder.btnReject.setOnClickListener { onReject(holder.adapterPosition) }
    }

    override fun getItemCount(): Int = documents.size
}