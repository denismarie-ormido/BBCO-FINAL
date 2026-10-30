package com.example.kayara

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        /*
         * Use the same notification fragment
         * so there is only ONE notification system.
         */

        supportFragmentManager
            .beginTransaction()
            .replace(
                android.R.id.content,
                NotificationsFragment()
            )
            .commit()
    }
}