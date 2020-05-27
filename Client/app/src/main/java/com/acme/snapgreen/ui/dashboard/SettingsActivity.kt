package com.acme.snapgreen.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.ui.login.LoginActivity
import com.firebase.ui.auth.AuthUI

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val signOutButton = findViewById<Button>(R.id.signOutSettingsButton)
        signOutButton.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                    startActivity(intent)
                    finish()
                }
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