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

        val waterButton = findViewById<Button>(R.id.water)

        waterButton.setOnClickListener {
            val intent = Intent(this, WaterActivity::class.java)
            startActivity(intent);
        }

        val scanner = findViewById<Button>(R.id.scanner)
        val myImageView = findViewById(R.id.imgview) as ImageView
        val myBitmap = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.puppy
        )
        myImageView.setImageBitmap(myBitmap)


        scanner.setOnClickListener{

            val detector = BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
                .build()
            if(!detector.isOperational())
            {
                txtview.setText("Could not set up the detector!")
            }

            val frame = Frame.Builder().setBitmap(myBitmap).build()
            val barcodes = detector.detect(frame)

            if(barcodes.size > 0)
            {
                val thisCode = barcodes.valueAt(0)
                txtview.setText(thisCode.rawValue)
            }
            else
            {
                txtview.setText("Failed to scan code, please try again!")
            }

        }

    }
}
