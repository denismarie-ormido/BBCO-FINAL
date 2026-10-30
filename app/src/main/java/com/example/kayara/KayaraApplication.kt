package com.example.kayara

import android.app.Application
import com.cloudinary.android.MediaManager

class KayaraApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = hashMapOf(
            "cloud_name" to "hq6hiqhq"
        )

        MediaManager.init(this, config)
    }
}