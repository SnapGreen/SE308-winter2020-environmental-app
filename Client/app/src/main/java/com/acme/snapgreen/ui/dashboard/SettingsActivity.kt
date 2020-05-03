package com.acme.snapgreen.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.ui.login.LoginActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, "Activity launched!", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_settings)

        val signOutButton = findViewById<Button>(R.id.signOutSettingsButton)
        signOutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val supportButton = findViewById<Button>(R.id.supportSettingsButton)
        supportButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)

        }

        val profileButton = findViewById<Button>(R.id.profileSettingsButton)
        profileButton.setOnClickListener {
            val profile = Intent(this, ProfileActivity::class.java)
            startActivity(profile)
            finish()

        }
    }
}