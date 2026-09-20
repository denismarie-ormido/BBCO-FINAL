package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FeedAdapter(
    private val posts: MutableList<FeedPostModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_PHOTO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (posts[position].postType == "text") TYPE_TEXT else TYPE_PHOTO
    }

    inner class TextPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPosterName: TextView = itemView.findViewById(R.id.tvPosterName)
        val tvTimeAgo: TextView = itemView.findViewById(R.id.tvTimeAgo)
        val tvAudienceBadge: TextView = itemView.findViewById(R.id.tvAudienceBadge)
        val tvTextContent: TextView = itemView.findViewById(R.id.tvTextContent)
    }

    inner class PhotoPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPosterAvatar: TextView = itemView.findViewById(R.id.tvPosterAvatar)
        val tvPosterName: TextView = itemView.findViewById(R.id.tvPosterName)
        val tvTimeAgo: TextView = itemView.findViewById(R.id.tvTimeAgo)
        val tvCaption: TextView = itemView.findViewById(R.id.tvCaption)
        val imgPost: ImageView = itemView.findViewById(R.id.imgPost)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        val tvCommentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_TEXT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed_text_post, parent, false)
            TextPostViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed_photo_post, parent, false)
            PhotoPostViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = posts[position]

        if (holder is TextPostViewHolder) {
            holder.tvPosterName.text = post.posterName
            holder.tvTimeAgo.text = post.timeAgo
            holder.tvTextContent.text = post.textContent

            if (post.audience == "admins_only") {
                holder.tvAudienceBadge.text = "ADMINS ONLY"
                holder.tvAudienceBadge.setBackgroundResource(R.drawable.bg_badge_admins)
                holder.tvAudienceBadge.setTextColor(holder.itemView.context.getColor(R.color.amber_text))
            } else {
                holder.tvAudienceBadge.text = "EVERYONE"
                holder.tvAudienceBadge.setBackgroundResource(R.drawable.bg_badge_everyone)
                holder.tvAudienceBadge.setTextColor(holder.itemView.context.getColor(R.color.mid_blue))
            }
        } else if (holder is PhotoPostViewHolder) {
            holder.tvPosterAvatar.text = post.avatarInitials
            holder.tvPosterName.text = post.posterName
            holder.tvTimeAgo.text = post.timeAgo
            holder.tvCaption.text = post.caption
            holder.tvLikeCount.text = post.likeCount.toString()
            holder.tvCommentCount.text = post.commentCount.toString()

            if (post.imageUrls.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(post.imageUrls[0])
                    .into(holder.imgPost)
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    // Bag-ong function — mao ni ang mag-dugang og bag-ong post sa taas sa listahan
    fun addPost(post: FeedPostModel) {
        posts.add(0, post)
        notifyItemInserted(0)
    }
}