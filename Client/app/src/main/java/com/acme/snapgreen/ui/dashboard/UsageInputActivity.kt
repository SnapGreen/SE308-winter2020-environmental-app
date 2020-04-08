package com.acme.snapgreen.ui.dashboard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R

class UsageInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, "Activity launched!", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_usage_input)
    }


}