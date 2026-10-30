package com.example.kayara

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AdminSignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    // ============================================================
    // FIVE REQUIRED DOCUMENTS
    // ============================================================

    private var document1Uri: Uri? = null
    private var document2Uri: Uri? = null
    private var document3Uri: Uri? = null
    private var document4Uri: Uri? = null
    private var document5Uri: Uri? = null

    private var document1FileName = ""
    private var document2FileName = ""
    private var document3FileName = ""
    private var document4FileName = ""
    private var document5FileName = ""

    private var currentDocumentSlot = 0

    private lateinit var btnSignUp: LinearLayout
    private lateinit var tvError: TextView

    private lateinit var tvSelectedFile1: TextView
    private lateinit var tvSelectedFile2: TextView
    private lateinit var tvSelectedFile3: TextView
    private lateinit var tvSelectedFile4: TextView
    private lateinit var tvSelectedFile5: TextView


    // ============================================================
    // DOCUMENT PICKER
    // ============================================================

    private val pickDocumentLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri == null) {
                return@registerForActivityResult
            }

            val fileName =
                getFileNameFromUri(uri)

            when (currentDocumentSlot) {

                1 -> {
                    document1Uri = uri
                    document1FileName = fileName

                    tvSelectedFile1.text = fileName
                    tvSelectedFile1.setTextColor(
                        getColor(R.color.dark_blue)
                    )
                }

                2 -> {
                    document2Uri = uri
                    document2FileName = fileName

                    tvSelectedFile2.text = fileName
                    tvSelectedFile2.setTextColor(
                        getColor(R.color.dark_blue)
                    )
                }

                3 -> {
                    document3Uri = uri
                    document3FileName = fileName

                    tvSelectedFile3.text = fileName
                    tvSelectedFile3.setTextColor(
                        getColor(R.color.dark_blue)
                    )
                }

                4 -> {
                    document4Uri = uri
                    document4FileName = fileName

                    tvSelectedFile4.text = fileName
                    tvSelectedFile4.setTextColor(
                        getColor(R.color.dark_blue)
                    )
                }

                5 -> {
                    document5Uri = uri
                    document5FileName = fileName

                    tvSelectedFile5.text = fileName
                    tvSelectedFile5.setTextColor(
                        getColor(R.color.dark_blue)
                    )
                }
            }

            updateDocumentStatus()
        }


    // ============================================================
    // ON CREATE
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_sign_up
        )

        auth =
            FirebaseAuth.getInstance()

        db =
            FirebaseFirestore.getInstance()

        initializeViews()
        setupListeners()
        updateDocumentStatus()
    }


    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private fun initializeViews() {

        btnSignUp =
            findViewById(R.id.btnSignUp)

        tvError =
            findViewById(R.id.tvError)

        tvSelectedFile1 =
            findViewById(R.id.tvSelectedFile1)

        tvSelectedFile2 =
            findViewById(R.id.tvSelectedFile2)

        tvSelectedFile3 =
            findViewById(R.id.tvSelectedFile3)

        tvSelectedFile4 =
            findViewById(R.id.tvSelectedFile4)

        tvSelectedFile5 =
            findViewById(R.id.tvSelectedFile5)
    }


    // ============================================================
    // LISTENERS
    // ============================================================

    private fun setupListeners() {

        val btnBack =
            findViewById<ImageView>(R.id.btnBack)

        val tvGoToLogin =
            findViewById<TextView>(R.id.tvGoToLogin)

        val uploadCard1 =
            findViewById<LinearLayout>(R.id.uploadCard1)

        val uploadCard2 =
            findViewById<LinearLayout>(R.id.uploadCard2)

        val uploadCard3 =
            findViewById<LinearLayout>(R.id.uploadCard3)

        val uploadCard4 =
            findViewById<LinearLayout>(R.id.uploadCard4)

        val uploadCard5 =
            findViewById<LinearLayout>(R.id.uploadCard5)

        val btnTogglePassword =
            findViewById<ImageView>(R.id.btnTogglePassword)

        val btnToggleConfirmPassword =
            findViewById<ImageView>(
                R.id.btnToggleConfirmPassword
            )

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val etConfirmPassword =
            findViewById<EditText>(
                R.id.etConfirmPassword
            )


        btnBack.setOnClickListener {
            finish()
        }


        tvGoToLogin.setOnClickListener {
            finish()
        }


        uploadCard1.setOnClickListener {

            currentDocumentSlot = 1

            pickDocumentLauncher.launch("*/*")
        }


        uploadCard2.setOnClickListener {

            currentDocumentSlot = 2

            pickDocumentLauncher.launch("*/*")
        }


        uploadCard3.setOnClickListener {

            currentDocumentSlot = 3

            pickDocumentLauncher.launch("*/*")
        }


        uploadCard4.setOnClickListener {

            currentDocumentSlot = 4

            pickDocumentLauncher.launch("*/*")
        }


        uploadCard5.setOnClickListener {

            currentDocumentSlot = 5

            pickDocumentLauncher.launch("*/*")
        }


        btnTogglePassword.setOnClickListener {

            isPasswordVisible =
                !isPasswordVisible

            togglePasswordVisibility(
                etPassword,
                btnTogglePassword,
                isPasswordVisible
            )
        }


        btnToggleConfirmPassword.setOnClickListener {

            isConfirmPasswordVisible =
                !isConfirmPasswordVisible

            togglePasswordVisibility(
                etConfirmPassword,
                btnToggleConfirmPassword,
                isConfirmPasswordVisible
            )
        }


        btnSignUp.setOnClickListener {

            submitApplication()
        }
    }


    // ============================================================
    // CHECK DOCUMENTS
    // ============================================================

    private fun updateDocumentStatus() {

        val uploadedCount =
            listOf(
                document1Uri,
                document2Uri,
                document3Uri,
                document4Uri,
                document5Uri
            ).count {
                it != null
            }


        if (uploadedCount == 5) {

            tvError.text =
                "All required organization documents are ready."

            tvError.setTextColor(
                getColor(R.color.navy)
            )

            tvError.visibility =
                View.VISIBLE

        } else {

            tvError.visibility =
                View.GONE
        }
    }


    // ============================================================
    // SUBMIT APPLICATION
    // ============================================================

    private fun submitApplication() {

        val etFullName =
            findViewById<EditText>(R.id.etFullName)

        val etOrgName =
            findViewById<EditText>(R.id.etOrgName)

        val etDescription =
            findViewById<EditText>(R.id.etDescription)

        val etUsername =
            findViewById<EditText>(R.id.etUsername)

        val etEmail =
            findViewById<EditText>(R.id.etEmail)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val etConfirmPassword =
            findViewById<EditText>(
                R.id.etConfirmPassword
            )


        val fullName =
            etFullName.text
                .toString()
                .trim()

        val organizationName =
            etOrgName.text
                .toString()
                .trim()

        val description =
            etDescription.text
                .toString()
                .trim()

        val username =
            etUsername.text
                .toString()
                .trim()
                .lowercase()

        val email =
            etEmail.text
                .toString()
                .trim()
                .lowercase()

        val password =
            etPassword.text
                .toString()
                .trim()

        val confirmPassword =
            etConfirmPassword.text
                .toString()
                .trim()


        // ========================================================
        // VALIDATION
        // ========================================================

        if (
            fullName.isEmpty() ||
            organizationName.isEmpty() ||
            description.isEmpty() ||
            username.isEmpty() ||
            email.isEmpty() ||
            password.isEmpty() ||
            confirmPassword.isEmpty()
        ) {

            showError(
                "Please fill in all required fields."
            )

            return
        }


        if (username.contains(" ")) {

            showError(
                "Organization username cannot contain spaces."
            )

            return
        }


        if (
            !Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {

            showError(
                "Please enter a valid email address."
            )

            return
        }


        if (password.length < 6) {

            showError(
                "Password must be at least 6 characters."
            )

            return
        }


        if (
            password !=
            confirmPassword
        ) {

            showError(
                "Passwords do not match."
            )

            return
        }


        // ========================================================
        // FIVE REQUIRED DOCUMENTS
        // ========================================================

        if (document1Uri == null) {

            showError(
                "Please upload the Police Clearance of the Lead Organizer."
            )

            return
        }


        if (document2Uri == null) {

            showError(
                "Please upload the Proposed Constitution and By-Laws."
            )

            return
        }


        if (document3Uri == null) {

            showError(
                "Please upload the List of Members with a minimum of 30 members."
            )

            return
        }


        if (document4Uri == null) {

            showError(
                "Please upload the Names of Two (2) Nominees for Organization Adviser."
            )

            return
        }


        if (document5Uri == null) {

            showError(
                "Please upload the Annual Work Plan of Activities."
            )

            return
        }


        setLoading(true)

        tvError.visibility =
            View.GONE


        // ========================================================
        // NEW FIX:
        // CHECK FIRESTORE FIRST BEFORE CREATING AUTH ACCOUNT
        // ========================================================

        checkExistingRegistration(
            username = username,
            email = email,
            onClear = {

                createAuthenticationAccount(
                    email = email,
                    password = password,
                    fullName = fullName,
                    organizationName = organizationName,
                    description = description,
                    username = username
                )
            }
        )
    }


    // ============================================================
    // CHECK EXISTING USER / APPLICATION
    // ============================================================

    private fun checkExistingRegistration(
        username: String,
        email: String,
        onClear: () -> Unit
    ) {

        // =========================================================
        // CHECK ORGANIZATION USERNAME IN USERS
        // =========================================================

        db.collection("users")
            .whereEqualTo(
                "username",
                username
            )
            .limit(1)
            .get()
            .addOnSuccessListener { usersSnapshot ->

                if (!usersSnapshot.isEmpty) {

                    setLoading(false)

                    showError(
                        "This organization username is already in use. Please choose another username."
                    )

                    return@addOnSuccessListener
                }


                // =====================================================
                // CHECK ORGANIZATION USERNAME IN APPLICATIONS
                // =====================================================

                db.collection("adminApplications")
                    .whereEqualTo(
                        "username",
                        username
                    )
                    .limit(1)
                    .get()
                    .addOnSuccessListener { applicationsSnapshot ->

                        if (!applicationsSnapshot.isEmpty) {

                            setLoading(false)

                            showError(
                                "This organization username is already registered or awaiting approval."
                            )

                            return@addOnSuccessListener
                        }


                        // Username is clear. Keep the existing email checks.
                        checkExistingEmailRegistration(
                            email = email,
                            onClear = onClear
                        )
                    }
                    .addOnFailureListener { error ->

                        setLoading(false)

                        showError(
                            "Unable to verify organization username: ${error.message}"
                        )
                    }
            }
            .addOnFailureListener { error ->

                setLoading(false)

                showError(
                    "Unable to verify organization username: ${error.message}"
                )
            }
    }


    private fun checkExistingEmailRegistration(
        email: String,
        onClear: () -> Unit
    ) {

        db.collection("users")
            .whereEqualTo(
                "email",
                email
            )
            .limit(1)
            .get()
            .addOnSuccessListener {
                    usersSnapshot ->


                if (
                    !usersSnapshot.isEmpty
                ) {

                    setLoading(false)

                    val document =
                        usersSnapshot.documents.first()

                    val role =
                        document.getString("role")
                            ?: ""

                    val status =
                        document.getString("status")
                            ?: ""


                    if (
                        role ==
                        "admin"
                    ) {

                        when (
                            status.lowercase()
                        ) {

                            "pending" -> {

                                showError(
                                    "An organization registration using this email is already pending Super Admin approval."
                                )
                            }


                            "approved" -> {

                                showError(
                                    "This email is already registered to an approved organization admin account."
                                )
                            }


                            "rejected" -> {

                                showError(
                                    "This email already has a rejected application. Please use Edit & Resubmit from the Admin Login screen."
                                )
                            }


                            else -> {

                                showError(
                                    "This email is already connected to an administrator account."
                                )
                            }
                        }

                    } else {

                        showError(
                            "This email is already being used by another account."
                        )
                    }

                    return@addOnSuccessListener
                }


                // =================================================
                // NO USER FOUND -> CHECK ADMIN APPLICATIONS
                // =================================================

                db.collection(
                    "adminApplications"
                )
                    .whereEqualTo(
                        "email",
                        email
                    )
                    .limit(1)
                    .get()
                    .addOnSuccessListener {
                            applicationsSnapshot ->


                        if (
                            !applicationsSnapshot.isEmpty
                        ) {

                            setLoading(false)

                            val application =
                                applicationsSnapshot
                                    .documents
                                    .first()

                            val status =
                                application
                                    .getString("status")
                                    ?: "pending"


                            when (
                                status.lowercase()
                            ) {

                                "pending" -> {

                                    showError(
                                        "An organization registration using this email is already pending Super Admin approval."
                                    )
                                }


                                "approved" -> {

                                    showError(
                                        "This email already belongs to an approved organization registration."
                                    )
                                }


                                "rejected" -> {

                                    showError(
                                        "A rejected application already exists for this email. Please use Edit & Resubmit."
                                    )
                                }


                                else -> {

                                    showError(
                                        "An organization application already exists for this email."
                                    )
                                }
                            }

                        } else {

                            // No Firestore record.
                            // Continue to Firebase Authentication.
                            onClear()
                        }
                    }
                    .addOnFailureListener {
                            error ->

                        setLoading(false)

                        showError(
                            "Unable to check existing applications: ${error.message}"
                        )
                    }
            }
            .addOnFailureListener {
                    error ->

                setLoading(false)

                showError(
                    "Unable to verify registration information: ${error.message}"
                )
            }
    }


    // ============================================================
    // CREATE AUTHENTICATION ACCOUNT
    // ============================================================

    private fun createAuthenticationAccount(
        email: String,
        password: String,
        fullName: String,
        organizationName: String,
        description: String,
        username: String
    ) {

        auth.createUserWithEmailAndPassword(
            email,
            password
        )
            .addOnSuccessListener {

                val firebaseUser =
                    auth.currentUser

                if (firebaseUser == null) {

                    setLoading(false)

                    showError(
                        "Unable to create account."
                    )

                    return@addOnSuccessListener
                }

                continueAdminRegistration(
                    uid = firebaseUser.uid,
                    fullName = fullName,
                    organizationName = organizationName,
                    description = description,
                    username = username,
                    email = email
                )
            }
            .addOnFailureListener { error ->

                // =====================================================
                // EMAIL EXISTS IN FIREBASE AUTH
                // BUT NO FIRESTORE REGISTRATION WAS FOUND EARLIER
                // =====================================================

                if (
                    error is
                            com.google.firebase.auth.FirebaseAuthUserCollisionException
                ) {

                    showProgressMessage(
                        "Existing account found. Verifying account..."
                    )

                    auth.signInWithEmailAndPassword(
                        email,
                        password
                    )
                        .addOnSuccessListener {

                            val firebaseUser =
                                auth.currentUser

                            if (firebaseUser == null) {

                                setLoading(false)

                                showError(
                                    "Unable to verify the existing account."
                                )

                                return@addOnSuccessListener
                            }

                            // =============================================
                            // EXISTING AUTH ACCOUNT IS VALID
                            // CONTINUE SAME ADMIN REGISTRATION
                            // =============================================

                            continueAdminRegistration(
                                uid = firebaseUser.uid,
                                fullName = fullName,
                                organizationName = organizationName,
                                description = description,
                                username = username,
                                email = email
                            )
                        }
                        .addOnFailureListener {

                            setLoading(false)

                            showError(
                                "This email already exists in Firebase Authentication. " +
                                        "The password entered does not match that account."
                            )
                        }

                    return@addOnFailureListener
                }


                setLoading(false)

                val message =
                    when {

                        error.message
                            ?.contains(
                                "badly formatted",
                                ignoreCase = true
                            ) == true -> {

                            "Please enter a valid email address."
                        }


                        error.message
                            ?.contains(
                                "password",
                                ignoreCase = true
                            ) == true -> {

                            "Password must be at least 6 characters."
                        }


                        else -> {

                            "Registration failed: ${error.message}"
                        }
                    }

                showError(message)
            }
    }

    private fun continueAdminRegistration(
        uid: String,
        fullName: String,
        organizationName: String,
        description: String,
        username: String,
        email: String
    ) {

        val applicationId =
            UUID.randomUUID()
                .toString()

        uploadDocumentsToCloudinary(
            uid = uid,
            applicationId = applicationId,
            fullName = fullName,
            organizationName = organizationName,
            description = description,
            username = username,
            email = email
        )
    }


    // ============================================================
    // UPLOAD ALL FIVE DOCUMENTS
    // ============================================================

    private fun uploadDocumentsToCloudinary(
        uid: String,
        applicationId: String,
        fullName: String,
        organizationName: String,
        description: String,
        username: String,
        email: String
    ) {

        val uri1 =
            document1Uri

        val uri2 =
            document2Uri

        val uri3 =
            document3Uri

        val uri4 =
            document4Uri

        val uri5 =
            document5Uri


        if (
            uri1 == null ||
            uri2 == null ||
            uri3 == null ||
            uri4 == null ||
            uri5 == null
        ) {

            setLoading(false)

            showError(
                "All 5 required organization documents must be uploaded."
            )

            return
        }


        showProgressMessage(
            "Uploading Police Clearance..."
        )


        uploadSingleDocument(
            uri = uri1,
            applicationId = applicationId,
            documentKey =
                "police_clearance_lead_organizer",
            documentLabel =
                "Police Clearance of Lead Organizer",
            documentFileName =
                document1FileName,
            onSuccess = {
                    url1,
                    publicId1,
                    fileName1 ->


                showProgressMessage(
                    "Uploading Proposed Constitution and By-Laws..."
                )


                uploadSingleDocument(
                    uri = uri2,
                    applicationId = applicationId,
                    documentKey =
                        "proposed_constitution_bylaws",
                    documentLabel =
                        "Proposed Constitution and By-Laws",
                    documentFileName =
                        document2FileName,
                    onSuccess = {
                            url2,
                            publicId2,
                            fileName2 ->


                        showProgressMessage(
                            "Uploading List of Members..."
                        )


                        uploadSingleDocument(
                            uri = uri3,
                            applicationId = applicationId,
                            documentKey =
                                "list_of_members",
                            documentLabel =
                                "List of Members (Minimum of 30 Members)",
                            documentFileName =
                                document3FileName,
                            onSuccess = {
                                    url3,
                                    publicId3,
                                    fileName3 ->


                                showProgressMessage(
                                    "Uploading Adviser Nominees..."
                                )


                                uploadSingleDocument(
                                    uri = uri4,
                                    applicationId = applicationId,
                                    documentKey =
                                        "adviser_nominees",
                                    documentLabel =
                                        "Names of Two (2) Nominees for Organization Adviser",
                                    documentFileName =
                                        document4FileName,
                                    onSuccess = {
                                            url4,
                                            publicId4,
                                            fileName4 ->


                                        showProgressMessage(
                                            "Uploading Annual Work Plan..."
                                        )


                                        uploadSingleDocument(
                                            uri = uri5,
                                            applicationId =
                                                applicationId,
                                            documentKey =
                                                "annual_work_plan",
                                            documentLabel =
                                                "Annual Work Plan of Activities",
                                            documentFileName =
                                                document5FileName,
                                            onSuccess = {
                                                    url5,
                                                    publicId5,
                                                    fileName5 ->


                                                saveApplicationToFirestore(
                                                    uid = uid,
                                                    applicationId =
                                                        applicationId,
                                                    fullName =
                                                        fullName,
                                                    organizationName =
                                                        organizationName,
                                                    description =
                                                        description,
                                                    username =
                                                        username,
                                                    email =
                                                        email,

                                                    document1Url =
                                                        url1,
                                                    document1PublicId =
                                                        publicId1,
                                                    document1FileName =
                                                        fileName1,

                                                    document2Url =
                                                        url2,
                                                    document2PublicId =
                                                        publicId2,
                                                    document2FileName =
                                                        fileName2,

                                                    document3Url =
                                                        url3,
                                                    document3PublicId =
                                                        publicId3,
                                                    document3FileName =
                                                        fileName3,

                                                    document4Url =
                                                        url4,
                                                    document4PublicId =
                                                        publicId4,
                                                    document4FileName =
                                                        fileName4,

                                                    document5Url =
                                                        url5,
                                                    document5PublicId =
                                                        publicId5,
                                                    document5FileName =
                                                        fileName5
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
    }


    // ============================================================
    // UPLOAD ONE DOCUMENT
    // ============================================================

    private fun uploadSingleDocument(
        uri: Uri,
        applicationId: String,
        documentKey: String,
        documentLabel: String,
        documentFileName: String,
        onSuccess: (
            String,
            String,
            String
        ) -> Unit
    ) {

        val publicId =
            "${applicationId}_$documentKey"


        MediaManager
            .get()
            .upload(uri)
            .unsigned(
                "kayara_upload"
            )
            .option(
                "folder",
                "kayara/admin_applications"
            )
            .option(
                "public_id",
                publicId
            )
            .callback(
                object :
                    UploadCallback {


                    override fun onStart(
                        requestId: String?
                    ) {
                    }


                    override fun onProgress(
                        requestId: String?,
                        bytes: Long,
                        totalBytes: Long
                    ) {
                    }


                    override fun onSuccess(
                        requestId: String?,
                        resultData:
                        MutableMap<Any?, Any?>?
                    ) {

                        val documentUrl =
                            resultData
                                ?.get(
                                    "secure_url"
                                )
                                ?.toString()


                        if (
                            documentUrl
                                .isNullOrBlank()
                        ) {

                            runOnUiThread {

                                setLoading(
                                    false
                                )

                                showError(
                                    "$documentLabel uploaded but no file URL was returned."
                                )
                            }

                            return
                        }


                        onSuccess(
                            documentUrl,
                            publicId,
                            documentFileName
                        )
                    }


                    override fun onError(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {

                        runOnUiThread {

                            setLoading(
                                false
                            )

                            showError(
                                "$documentLabel upload failed: ${
                                    error?.description
                                        ?: "Unknown error"
                                }"
                            )
                        }
                    }


                    override fun onReschedule(
                        requestId: String?,
                        error: ErrorInfo?
                    ) {

                        runOnUiThread {

                            showProgressMessage(
                                "Retrying $documentLabel upload..."
                            )
                        }
                    }
                }
            )
            .dispatch()
    }


    // ============================================================
    // SAVE APPLICATION
    // ============================================================

    private fun saveApplicationToFirestore(
        uid: String,
        applicationId: String,
        fullName: String,
        organizationName: String,
        description: String,
        username: String,
        email: String,

        document1Url: String,
        document1PublicId: String,
        document1FileName: String,

        document2Url: String,
        document2PublicId: String,
        document2FileName: String,

        document3Url: String,
        document3PublicId: String,
        document3FileName: String,

        document4Url: String,
        document4PublicId: String,
        document4FileName: String,

        document5Url: String,
        document5PublicId: String,
        document5FileName: String
    ) {

        showProgressMessage(
            "Saving registration..."
        )


        val now =
            Timestamp.now()


        val documents =
            listOf(

                hashMapOf(
                    "key" to
                            "police_clearance_lead_organizer",
                    "label" to
                            "Police Clearance of Lead Organizer",
                    "fileName" to
                            document1FileName,
                    "url" to
                            document1Url,
                    "publicId" to
                            document1PublicId
                ),

                hashMapOf(
                    "key" to
                            "proposed_constitution_bylaws",
                    "label" to
                            "Proposed Constitution and By-Laws",
                    "fileName" to
                            document2FileName,
                    "url" to
                            document2Url,
                    "publicId" to
                            document2PublicId
                ),

                hashMapOf(
                    "key" to
                            "list_of_members",
                    "label" to
                            "List of Members (Minimum of 30 Members)",
                    "fileName" to
                            document3FileName,
                    "url" to
                            document3Url,
                    "publicId" to
                            document3PublicId
                ),

                hashMapOf(
                    "key" to
                            "adviser_nominees",
                    "label" to
                            "Names of Two (2) Nominees for Organization Adviser",
                    "fileName" to
                            document4FileName,
                    "url" to
                            document4Url,
                    "publicId" to
                            document4PublicId
                ),

                hashMapOf(
                    "key" to
                            "annual_work_plan",
                    "label" to
                            "Annual Work Plan of Activities",
                    "fileName" to
                            document5FileName,
                    "url" to
                            document5Url,
                    "publicId" to
                            document5PublicId
                )
            )


        val applicationData =
            hashMapOf(

                "applicationId" to
                        applicationId,

                "uid" to
                        uid,

                "fullName" to
                        fullName,

                "username" to
                        username,

                "email" to
                        email,

                "organizationName" to
                        organizationName,

                "organizationDescription" to
                        description,

                "organizationId" to
                        "",

                "documents" to
                        documents,

                "documentUrl" to
                        document1Url,

                "documentPublicId" to
                        document1PublicId,

                "role" to
                        "admin",

                "status" to
                        "pending",

                "rejectionReason" to
                        "",

                "createdAt" to
                        now,

                "updatedAt" to
                        now
            )


        db.collection(
            "adminApplications"
        )
            .document(
                applicationId
            )
            .set(
                applicationData
            )
            .addOnSuccessListener {


                val userData =
                    hashMapOf(

                        "uid" to
                                uid,

                        "fullName" to
                                fullName,

                        "username" to
                                username,

                        "email" to
                                email,

                        "role" to
                                "admin",

                        "status" to
                                "pending",

                        "organizationId" to
                                "",

                        "organizationName" to
                                organizationName,

                        "applicationId" to
                                applicationId,

                        "createdAt" to
                                now
                    )


                db.collection(
                    "users"
                )
                    .document(
                        uid
                    )
                    .set(
                        userData
                    )
                    .addOnSuccessListener {


                        setLoading(
                            false
                        )


                        Toast.makeText(
                            this,
                            "Registration submitted successfully. Please wait for Super Admin approval.",
                            Toast.LENGTH_LONG
                        ).show()


                        auth.signOut()

                        finish()
                    }
                    .addOnFailureListener {
                            error ->


                        setLoading(
                            false
                        )


                        showError(
                            "Application saved, but user profile failed: ${error.message}"
                        )
                    }
            }
            .addOnFailureListener {
                    error ->


                setLoading(
                    false
                )


                showError(
                    "Failed to save registration: ${error.message}"
                )
            }
    }


    // ============================================================
    // PASSWORD VISIBILITY
    // ============================================================

    private fun togglePasswordVisibility(
        editText: EditText,
        icon: ImageView,
        visible: Boolean
    ) {

        if (
            visible
        ) {

            editText.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            icon.setImageResource(
                R.drawable.ic_eye_off
            )

        } else {

            editText.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD

            icon.setImageResource(
                R.drawable.ic_eye
            )
        }


        editText.setSelection(
            editText.text.length
        )
    }


    // ============================================================
    // GET ACTUAL FILE NAME
    // ============================================================

    private fun getFileNameFromUri(
        uri: Uri
    ): String {

        var name =
            "Selected document"


        val cursor =
            contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )


        cursor?.use {

            if (
                it.moveToFirst()
            ) {

                val nameIndex =
                    it.getColumnIndex(
                        android.provider
                            .OpenableColumns
                            .DISPLAY_NAME
                    )


                if (
                    nameIndex != -1
                ) {

                    name =
                        it.getString(
                            nameIndex
                        )
                }
            }
        }


        return name
    }


    // ============================================================
    // LOADING
    // ============================================================

    private fun setLoading(
        loading: Boolean
    ) {

        btnSignUp.isEnabled =
            !loading

        btnSignUp.alpha =
            if (
                loading
            ) {
                0.6f
            } else {
                1f
            }
    }


    // ============================================================
    // PROGRESS MESSAGE
    // ============================================================

    private fun showProgressMessage(
        message: String
    ) {

        runOnUiThread {

            tvError.text =
                message

            tvError.setTextColor(
                getColor(
                    R.color.navy
                )
            )

            tvError.visibility =
                View.VISIBLE
        }
    }


    // ============================================================
    // ERROR
    // ============================================================

    private fun showError(
        message: String
    ) {

        tvError.text =
            message

        tvError.setTextColor(
            getColor(
                android.R.color
                    .holo_red_dark
            )
        )

        tvError.visibility =
            View.VISIBLE
    }
}
