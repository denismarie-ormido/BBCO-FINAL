package com.example.kayara

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_ORG_ID = "organizationId"
        const val EXTRA_ORG_NAME = "organizationName"
        const val EXTRA_AS_ADMIN = "asAdmin"

        const val EXTRA_AS_SUPER_ADMIN = "asSuperAdmin"

        const val ADMINS_ROOM_ID = "admins_all"

        private const val MAX_MESSAGES = 200
    }

    private val auth =
        FirebaseAuth.getInstance()

    private val db =
        FirebaseFirestore.getInstance()

    private var messagesListener:
            ListenerRegistration? = null

    private var organizationId = ""
    private var organizationName = ""

    private var asAdmin = false
    private var asSuperAdmin = false

    private var currentUid = ""

    // =========================================================
    // SENDER INFO
    // =========================================================

    private var senderName = "Member"
    private var senderRole = ""
    private var senderPhotoUrl = ""

    // =========================================================
    // VIEWS
    // =========================================================

    private lateinit var chatAdapter:
            ChatAdapter

    private lateinit var rvChat:
            RecyclerView

    private lateinit var etMessage:
            EditText

    private lateinit var btnSend:
            ImageView

    private lateinit var tvEmpty:
            TextView

    private var lastCount = -1

    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_chat
        )

        organizationId =
            intent
                .getStringExtra(
                    EXTRA_ORG_ID
                )
                ?.trim()
                ?: ""

        organizationName =
            intent
                .getStringExtra(
                    EXTRA_ORG_NAME
                )
                ?.trim()
                ?: ""

        asAdmin =
            intent.getBooleanExtra(
                EXTRA_AS_ADMIN,
                false
            )

        asSuperAdmin =
            intent.getBooleanExtra(
                EXTRA_AS_SUPER_ADMIN,
                false
            )

        val uid =
            auth.currentUser?.uid

        if (
            uid == null ||
            organizationId.isBlank()
        ) {

            Toast.makeText(
                this,
                "Unable to open the chat.",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        currentUid =
            uid

        // =====================================================
        // VIEWS
        // =====================================================

        rvChat =
            findViewById(
                R.id.rvChat
            )

        etMessage =
            findViewById(
                R.id.etChatMessage
            )

        btnSend =
            findViewById(
                R.id.btnChatSend
            )

        tvEmpty =
            findViewById(
                R.id.tvChatEmpty
            )

        findViewById<ImageView>(
            R.id.btnChatBack
        ).setOnClickListener {

            finish()
        }

        // =====================================================
        // HEADER
        // =====================================================

        findViewById<TextView>(
            R.id.tvChatTitle
        ).text =
            organizationName.ifBlank {
                "Organization"
            }

        findViewById<TextView>(
            R.id.tvChatOrgAvatar
        ).text =
            getOrgInitials(
                organizationName
            )

        loadOrganizationAvatar()

        // =====================================================
        // ADMINS GROUP CHAT HEADER
        // =====================================================

        if (
            organizationId ==
            ADMINS_ROOM_ID
        ) {

            findViewById<TextView>(
                R.id.tvChatTitle
            ).text =
                "Admins Group Chat"

            findViewById<TextView>(
                R.id.tvChatOrgAvatar
            ).text =
                "ADM"

            findViewById<TextView>(
                R.id.tvChatSubtitle
            ).text =
                "Super Admin + all organization admins"

            etMessage.hint =
                "Message all admins..."
        }

        // =====================================================
        // CHAT LIST
        // =====================================================

        chatAdapter =
            ChatAdapter(
                currentUid
            )

        rvChat.layoutManager =
            LinearLayoutManager(
                this
            ).apply {

                stackFromEnd =
                    true
            }

        rvChat.adapter =
            chatAdapter

        // =====================================================
        // SEND BUTTON
        // =====================================================

        btnSend.isEnabled =
            false

        btnSend.alpha =
            0.5f

        btnSend.setOnClickListener {

            sendMessage()
        }

        // =====================================================
        // VERIFY ACCESS
        // =====================================================

        verifyAccessAndStart()
    }

    // =========================================================
    // ORGANIZATION AVATAR
    // =========================================================

    private fun loadOrganizationAvatar() {

        if (
            organizationId ==
            ADMINS_ROOM_ID
        ) {

            return
        }

        db.collection(
            "organizations"
        )
            .document(
                organizationId
            )
            .get()
            .addOnSuccessListener { document ->

                if (
                    isDestroyed
                ) {

                    return@addOnSuccessListener
                }

                val avatarUrl =
                    document.getString(
                        "avatarUrl"
                    )
                        ?: ""

                if (
                    avatarUrl.isNotBlank()
                ) {

                    val img =
                        findViewById<ImageView>(
                            R.id.imgChatOrgAvatar
                        )

                    img.visibility =
                        View.VISIBLE

                    findViewById<TextView>(
                        R.id.tvChatOrgAvatar
                    ).visibility =
                        View.GONE

                    Glide.with(
                        this
                    )
                        .load(
                            avatarUrl
                        )
                        .circleCrop()
                        .into(
                            img
                        )
                }
            }
    }

    // =========================================================
    // VERIFY ACCESS
    // =========================================================

    private fun verifyAccessAndStart() {

        db.collection(
            "users"
        )
            .document(
                currentUid
            )
            .get()
            .addOnSuccessListener { userDoc ->

                if (
                    isDestroyed
                ) {

                    return@addOnSuccessListener
                }

                senderPhotoUrl =
                    userDoc.getString(
                        "profileImageUrl"
                    )
                        ?: ""

                // =================================================
                // SUPER ADMIN
                // =================================================

                if (
                    asSuperAdmin
                ) {

                    senderName =
                        "Super Admin"

                    senderRole =
                        "Super Admin"

                    startChat()

                    return@addOnSuccessListener
                }

                // =================================================
                // ORGANIZATION ADMIN
                // =================================================

                if (
                    asAdmin
                ) {

                    val isApprovedAdmin =
                        userDoc.getString(
                            "role"
                        ) == "admin" &&
                                userDoc.getString(
                                    "status"
                                ) == "approved"

                    if (
                        !isApprovedAdmin
                    ) {

                        Toast.makeText(
                            this,
                            "Only approved admins can join this chat.",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()

                        return@addOnSuccessListener
                    }

                    val adminOrganizationId =
                        userDoc.getString(
                            "organizationId"
                        )
                            ?.trim()
                            ?: ""

                    val savedOrganizationName =
                        userDoc.getString(
                            "organizationName"
                        )
                            ?.trim()
                            ?: ""

                    // =================================================
                    // FALLBACK IF NO ORG ID
                    // =================================================

                    if (
                        adminOrganizationId.isBlank()
                    ) {

                        senderName =
                            savedOrganizationName
                                .ifBlank {
                                    "Organization"
                                }

                        senderRole =
                            "ADMIN"

                        startChat()

                        return@addOnSuccessListener
                    }

                    // =================================================
                    // GET ORGANIZATION NAME
                    // =================================================

                    db.collection(
                        "organizations"
                    )
                        .document(
                            adminOrganizationId
                        )
                        .get()
                        .addOnSuccessListener { orgDocument ->

                            if (
                                isDestroyed
                            ) {

                                return@addOnSuccessListener
                            }

                            val realOrganizationName =
                                orgDocument
                                    .getString(
                                        "orgName"
                                    )
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: orgDocument
                                        .getString(
                                            "organizationName"
                                        )
                                        ?.takeIf {
                                            it.isNotBlank()
                                        }
                                    ?: savedOrganizationName
                                        .takeIf {
                                            it.isNotBlank()
                                        }
                                    ?: "Organization"

                            // =========================================
                            // IMPORTANT:
                            // USE ORGANIZATION NAME, NOT ADMIN NAME
                            // =========================================

                            senderName =
                                realOrganizationName

                            senderRole =
                                "ADMIN"

                            startChat()
                        }
                        .addOnFailureListener {

                            senderName =
                                savedOrganizationName
                                    .ifBlank {
                                        "Organization"
                                    }

                            senderRole =
                                "ADMIN"

                            startChat()
                        }

                    return@addOnSuccessListener
                }

                // =================================================
                // STUDENT / MEMBER
                // =================================================

                senderName =
                    userDoc.getString(
                        "fullName"
                    )
                        ?: auth
                            .currentUser
                            ?.displayName
                                ?: "Member"

                db.collection(
                    "memberships"
                )
                    .document(
                        "${currentUid}_$organizationId"
                    )
                    .get()
                    .addOnSuccessListener { membership ->

                        if (
                            isDestroyed
                        ) {

                            return@addOnSuccessListener
                        }

                        val approved =
                            membership.exists() &&
                                    membership.getString(
                                        "status"
                                    ) == "approved"

                        if (
                            !approved
                        ) {

                            Toast.makeText(
                                this,
                                "Only approved members can join this chat.",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()

                            return@addOnSuccessListener
                        }

                        senderRole =
                            if (
                                membership.getString(
                                    "role"
                                ) == "officer"
                            ) {

                                membership.getString(
                                    "position"
                                )
                                    ?: ""

                            } else {

                                ""
                            }

                        startChat()
                    }
                    .addOnFailureListener { error ->

                        Toast.makeText(
                            this,
                            "Unable to open the chat: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Unable to open the chat: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
    }

    // =========================================================
    // START CHAT
    // =========================================================

    private fun startChat() {

        btnSend.isEnabled =
            true

        btnSend.alpha =
            1f

        listenToMessages()
    }

    // =========================================================
    // LISTEN TO MESSAGES
    // =========================================================

    private fun listenToMessages() {

        messagesListener?.remove()

        messagesListener =
            db.collection(
                "chats"
            )
                .document(
                    organizationId
                )
                .collection(
                    "messages"
                )
                .orderBy(
                    "createdAt",
                    Query.Direction.ASCENDING
                )
                .limitToLast(
                    MAX_MESSAGES.toLong()
                )
                .addSnapshotListener { snapshot, error ->

                    if (
                        isDestroyed
                    ) {

                        return@addSnapshotListener
                    }

                    if (
                        error != null
                    ) {

                        Toast.makeText(
                            this,
                            "Failed to load messages: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        return@addSnapshotListener
                    }

                    if (
                        snapshot == null
                    ) {

                        return@addSnapshotListener
                    }

                    val messages =
                        snapshot.documents.map { document ->

                            val time =
                                document
                                    .getTimestamp(
                                        "createdAt",
                                        DocumentSnapshot
                                            .ServerTimestampBehavior
                                            .ESTIMATE
                                    )
                                    ?.toDate()
                                    ?.time
                                    ?: System.currentTimeMillis()

                            ChatMessageModel(

                                id =
                                    document.id,

                                senderId =
                                    document.getString(
                                        "senderId"
                                    )
                                        ?: "",

                                senderName =
                                    document.getString(
                                        "senderName"
                                    )
                                        ?: "Member",

                                senderRole =
                                    document.getString(
                                        "senderRole"
                                    )
                                        ?: "",

                                senderPhotoUrl =
                                    document.getString(
                                        "senderPhotoUrl"
                                    )
                                        ?: "",

                                text =
                                    document.getString(
                                        "text"
                                    )
                                        ?: "",

                                timeMillis =
                                    time
                            )
                        }

                    chatAdapter.submit(
                        messages
                    )

                    tvEmpty.visibility =
                        if (
                            messages.isEmpty()
                        ) {

                            View.VISIBLE

                        } else {

                            View.GONE
                        }

                    if (
                        messages.size != lastCount &&
                        messages.isNotEmpty()
                    ) {

                        rvChat.scrollToPosition(
                            messages.size - 1
                        )
                    }

                    lastCount =
                        messages.size
                }
    }

    // =========================================================
    // SEND MESSAGE
    // =========================================================

    private fun sendMessage() {

        val text =
            etMessage.text
                .toString()
                .trim()

        if (
            text.isEmpty()
        ) {

            return
        }

        etMessage.text.clear()

        val data =
            hashMapOf<String, Any>(

                "senderId" to
                        currentUid,

                "senderName" to
                        senderName,

                "senderRole" to
                        senderRole,

                "senderPhotoUrl" to
                        senderPhotoUrl,

                "text" to
                        text,

                "createdAt" to
                        FieldValue.serverTimestamp()
            )

        db.collection(
            "chats"
        )
            .document(
                organizationId
            )
            .collection(
                "messages"
            )
            .add(
                data
            )
            .addOnSuccessListener { messageDocument ->

                // Student/member message ra ang
                // mo-notify sa Admin.

                if (
                    !asAdmin &&
                    !asSuperAdmin &&
                    organizationId !=
                    ADMINS_ROOM_ID
                ) {

                    val preview =
                        if (
                            text.length > 70
                        ) {

                            text.take(
                                70
                            ) + "..."

                        } else {

                            text
                        }

                    AdminNotificationHelper.create(

                        organizationId =
                            organizationId,

                        organizationName =
                            organizationName,

                        title =
                            "New Group Chat Message",

                        message =
                            "$senderName: $preview",

                        type =
                            "chat",

                        referenceId =
                            messageDocument.id,

                        studentId =
                            currentUid
                    )
                }
            }
            .addOnFailureListener { error ->

                if (
                    isDestroyed
                ) {

                    return@addOnFailureListener
                }

                Toast.makeText(
                    this,
                    "Message not sent: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =========================================================
    // ORGANIZATION INITIALS
    // =========================================================

    private fun getOrgInitials(
        name: String
    ): String {

        val words =
            name.trim()
                .split(
                    Regex("\\s+")
                )
                .filter {
                    it.isNotBlank()
                }

        return when {

            words.isEmpty() -> {

                "ORG"
            }

            words.size == 1 -> {

                words[0]
                    .take(3)
                    .uppercase()
            }

            else -> {

                words
                    .take(3)
                    .map {
                        it.first()
                    }
                    .joinToString(
                        ""
                    )
                    .uppercase()
            }
        }
    }

    // =========================================================
    // DESTROY
    // =========================================================

    override fun onDestroy() {

        messagesListener?.remove()

        messagesListener =
            null

        super.onDestroy()
    }
}