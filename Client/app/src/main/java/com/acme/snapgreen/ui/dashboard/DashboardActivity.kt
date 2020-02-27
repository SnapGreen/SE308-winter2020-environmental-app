package com.acme.snapgreen.ui.dashboard

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.size
import androidx.lifecycle.ViewModelProviders
import com.acme.snapgreen.R
import com.acme.snapgreen.ui.scanner.PreviewActivity
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_dashboard.*

const val EXTRA_MESSAGE = "com.acme.snapgreen.MESSAGE"

class DashboardActivity : AppCompatActivity() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NetworkManager.setInstanceContext(this)

        dashboardViewModel = ViewModelProviders.of(this, DashboardViewModelFactory())
            .get(DashboardViewModel::class.java)
        setContentView(R.layout.activity_dashboard)

        // get the username from the login activity
        val username = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val welcomeView = findViewById<TextView>(R.id.textView).apply {
            text = "Welcome " + username
        }


        val waterButton = findViewById<Button>(R.id.usage_input)

        waterButton.setOnClickListener {
            val intent = Intent(this, UsageInputActivity::class.java)
            startActivity(intent)
        }

        val shopListButton = findViewById<Button>(R.id.shopping_list)

        shopListButton.setOnClickListener {
            val intent = Intent(this, ShoppingListActivity::class.java)
            startActivity(intent)
        }

        val inviteButton = findViewById<Button>(R.id.invite)

        inviteButton.setOnClickListener {
            val intent = Intent(this, InviteActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<Button>(R.id.settings)

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val scannerButton = findViewById<Button>(R.id.scan)

        scannerButton.setOnClickListener{
            val intent = Intent(this, PreviewActivity::class.java)
            startActivity(intent)
        }

    }
}
