package com.example.kayara

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RequirementAdapter(
    private val requirements: MutableList<RequirementModel>,
    // Optional: long-press sa file (pananglitan Delete) - para sa admin ra
    private val onLongClick: ((RequirementModel) -> Unit)? = null
) : RecyclerView.Adapter<RequirementAdapter.ReqViewHolder>() {

    inner class ReqViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView =
            itemView.findViewById(R.id.tvDocName)

        val tvMeta: TextView =
            itemView.findViewById(R.id.tvDocMeta)

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvDocStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReqViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_requirement_doc,
                    parent,
                    false
                )

        return ReqViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReqViewHolder,
        position: Int
    ) {

        val doc =
            requirements[position]

        holder.tvName.text =
            doc.fileName

        holder.tvMeta.text =
            "Uploaded ${doc.uploadDate} · ${doc.fileSize}"


        // =====================================================
        // STATUS
        // =====================================================

        when (doc.status) {

            "approved" -> {

                holder.tvStatus.text =
                    "APPROVED"

                holder.tvStatus.setBackgroundResource(
                    R.drawable.bg_tag_active
                )

                holder.tvStatus.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.teal_deep
                    )
                )
            }

            "pending" -> {

                holder.tvStatus.text =
                    "UNDER REVIEW"

                holder.tvStatus.setBackgroundResource(
                    R.drawable.bg_tag_pending
                )

                holder.tvStatus.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.amber_text
                    )
                )
            }

            else -> {

                holder.tvStatus.text =
                    "REJECTED"

                holder.tvStatus.setBackgroundResource(
                    R.drawable.bg_tag_overdue
                )

                holder.tvStatus.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.coral_text
                    )
                )
            }
        }


        // =====================================================
        // CLICK DOCUMENT
        // =====================================================

        holder.itemView.setOnClickListener {

            val url =
                doc.fileUrl.trim()

            if (url.isBlank()) {

                Toast.makeText(
                    holder.itemView.context,
                    "This document does not have a file link.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            try {

                val intent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )

                holder.itemView.context.startActivity(
                    intent
                )

            } catch (
                e: Exception
            ) {

                Toast.makeText(
                    holder.itemView.context,
                    "Unable to open this document.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        // =====================================================
        // LONG PRESS (Delete, admin ra)
        // =====================================================

        if (onLongClick != null) {

            holder.itemView.setOnLongClickListener {

                onLongClick.invoke(doc)

                true
            }

        } else {

            holder.itemView.setOnLongClickListener(null)
        }
    }

    override fun getItemCount(): Int =
        requirements.size


    // =========================================================
    // ADD DOCUMENT
    // =========================================================

    fun addDocument(
        doc: RequirementModel
    ) {

        requirements.add(
            0,
            doc
        )

        notifyItemInserted(0)
    }
}