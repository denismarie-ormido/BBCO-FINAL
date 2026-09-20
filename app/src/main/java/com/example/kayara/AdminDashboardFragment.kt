package com.example.kayara

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminDashboardFragment : Fragment() {

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var rvFeed: RecyclerView

    private lateinit var photoPreviewContainer: LinearLayout
    private lateinit var photoPreviewList: LinearLayout
    private lateinit var tvPhotoCount: TextView

    private val selectedPhotoUris = mutableListOf<Uri>()

    /*
     * MULTIPLE PHOTO PICKER
     * Maximum = 100 photos
     */
    private val pickPhotoLauncher =
        registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(100)
        ) { uris: List<Uri> ->

            if (uris.isNotEmpty()) {

                selectedPhotoUris.clear()
                selectedPhotoUris.addAll(uris)

                showSelectedPhotos()

                photoPreviewContainer.visibility = View.VISIBLE
            }
        }

    /*
     * Sample posts
     *
     * TODO:
     * Replace with Firestore query later.
     */
    private val feedPosts = mutableListOf(

        FeedPostModel(
            postType = "text",
            posterName = "Engr. Santos",
            posterRole = "Super Admin",
            timeAgo = "2 hours ago",
            audience = "everyone",
            textContent =
                "Reminder: General Assembly on September 5, 1:00 PM at the BISU Balilihan Gymnasium."
        ),

        FeedPostModel(
            postType = "photo",
            posterName = "Red Cross Youth",
            posterRole = "You",
            timeAgo = "3 hours ago",
            avatarInitials = "RCY",
            caption =
                "Blood Donation Drive 2026 — thank you to over 120 donors!",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1615461066841-6116e61058f4?w=600&h=400&fit=crop"
            ),
            likeCount = 214,
            commentCount = 18
        ),

        FeedPostModel(
            postType = "text",
            posterName = "Engr. Santos",
            posterRole = "Super Admin",
            timeAgo = "5 hours ago",
            audience = "admins_only",
            textContent =
                "All org advisers: please submit your updated officer roster on or before September 15."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_admin_dashboard,
            container,
            false
        )

        /*
         * FIND VIEWS
         */
        val etComposeText =
            view.findViewById<EditText>(R.id.etComposeText)

        val btnAddPhoto =
            view.findViewById<LinearLayout>(R.id.btnAddPhoto)

        val btnRemovePhoto =
            view.findViewById<TextView>(R.id.btnRemovePhoto)

        val btnSubmitPost =
            view.findViewById<TextView>(R.id.btnSubmitPost)

        photoPreviewContainer =
            view.findViewById(R.id.photoPreviewContainer)

        photoPreviewList =
            view.findViewById(R.id.photoPreviewList)

        tvPhotoCount =
            view.findViewById(R.id.tvPhotoCount)

        rvFeed =
            view.findViewById(R.id.rvFeed)


        /*
         * ADD PHOTO
         */
        btnAddPhoto.setOnClickListener {

            pickPhotoLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }


        /*
         * REMOVE ALL SELECTED PHOTOS
         */
        btnRemovePhoto.setOnClickListener {

            selectedPhotoUris.clear()

            photoPreviewList.removeAllViews()

            tvPhotoCount.text = ""

            photoPreviewContainer.visibility = View.GONE
        }


        /*
         * POST
         */
        btnSubmitPost.setOnClickListener {

            val text =
                etComposeText.text.toString().trim()

            /*
             * No text AND no photos
             */
            if (
                text.isEmpty() &&
                selectedPhotoUris.isEmpty()
            ) {

                Toast.makeText(
                    requireContext(),
                    "Please write something or add a photo",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            /*
             * PHOTO POST
             */
            val newPost: FeedPostModel

            if (selectedPhotoUris.isNotEmpty()) {

                newPost = FeedPostModel(
                    postType = "photo",
                    posterName = "Red Cross Youth",
                    posterRole = "You",
                    timeAgo = "Just now",
                    avatarInitials = "RCY",
                    caption = text,

                    /*
                     * ALL SELECTED PHOTOS
                     */
                    imageUrls =
                        selectedPhotoUris.map {
                            it.toString()
                        },

                    likeCount = 0,
                    commentCount = 0
                )

            } else {

                /*
                 * TEXT ONLY POST
                 */
                newPost = FeedPostModel(
                    postType = "text",
                    posterName = "Red Cross Youth",
                    posterRole = "You",
                    timeAgo = "Just now",
                    avatarInitials = "RCY",
                    textContent = text,
                    likeCount = 0,
                    commentCount = 0
                )
            }


            /*
             * ADD NEW POST TO FEED
             *
             * Index 0 = latest post on top
             */
            feedPosts.add(0, newPost)

            feedAdapter.notifyItemInserted(0)

            rvFeed.scrollToPosition(0)


            /*
             * CLEAR COMPOSER
             */
            etComposeText.text.clear()

            selectedPhotoUris.clear()

            photoPreviewList.removeAllViews()

            tvPhotoCount.text = ""

            photoPreviewContainer.visibility = View.GONE


            Toast.makeText(
                requireContext(),
                "Posted successfully!",
                Toast.LENGTH_SHORT
            ).show()


            /*
             * TODO FIREBASE
             *
             * Later:
             *
             * 1. Upload every selected photo
             *    to Firebase Storage
             *
             * 2. Get download URLs
             *
             * 3. Save post to Firestore
             *
             * Example:
             *
             * {
             *     text: "...",
             *     photoUrls: [...],
             *     organizationId: "...",
             *     timestamp: ...
             * }
             */
        }


        /*
         * FEED
         */
        rvFeed.layoutManager =
            LinearLayoutManager(requireContext())

        feedAdapter =
            FeedAdapter(feedPosts)

        rvFeed.adapter = feedAdapter


        return view
    }


    /*
     * DISPLAY SELECTED PHOTO THUMBNAILS
     */
    private fun showSelectedPhotos() {

        photoPreviewList.removeAllViews()

        selectedPhotoUris.forEach { uri ->

            val imageView =
                ImageView(requireContext())

            val size =
                90.dpToPx()

            val params =
                LinearLayout.LayoutParams(
                    size,
                    size
                )

            params.setMargins(
                0,
                0,
                8.dpToPx(),
                0
            )

            imageView.layoutParams = params

            imageView.scaleType =
                ImageView.ScaleType.CENTER_CROP

            imageView.setImageURI(uri)

            imageView.clipToOutline = true

            photoPreviewList.addView(imageView)
        }


        /*
         * PHOTO COUNT
         */
        tvPhotoCount.text =
            if (selectedPhotoUris.size == 1) {
                "1 photo selected"
            } else {
                "${selectedPhotoUris.size} photos selected"
            }
    }


    /*
     * DP TO PX
     */
    private fun Int.dpToPx(): Int {

        return (
                this *
                        resources.displayMetrics.density
                ).toInt()
    }
}