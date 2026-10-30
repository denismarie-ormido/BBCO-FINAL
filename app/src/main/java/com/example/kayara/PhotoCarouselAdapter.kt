package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoCarouselAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<PhotoCarouselAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView =
            itemView.findViewById(
                R.id.carouselImage
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_photo_carousel,
                    parent,
                    false
                )

        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PhotoViewHolder,
        position: Int
    ) {

        Glide.with(holder.itemView.context)
            .load(imageUrls[position])

            // IMPORTANT:
            // Do not use centerCrop because it cuts the photo.
            .fitCenter()

            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}