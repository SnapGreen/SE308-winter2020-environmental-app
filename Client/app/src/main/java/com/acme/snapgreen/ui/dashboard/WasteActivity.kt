package com.acme.snapgreen.ui.dashboard

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.size
import androidx.lifecycle.ViewModelProviders
import com.acme.snapgreen.R
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_dashboard.*


class WasteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, "Activity Launched!", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_waste)
    }
}