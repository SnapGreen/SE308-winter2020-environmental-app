package com.acme.snapgreen.ui.scanner

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.ui.dashboard.EXTRA_MESSAGE
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

/**
 * UI class for displaying the results of a bar code scan. We should show environmental impact
 * of the product as well as other applicable information.
 */
class ScanResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, "Barcode result!", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_scan_result)


        val barCodeString = intent.getStringExtra(EXTRA_MESSAGE)
        val barcodeTextView = findViewById<TextView>(R.id.barcodeResult).apply {
            text = barCodeString
        }

        val url = "http://10.0.2.2:8080/barcode"
        val jsonObj = JSONObject()
        jsonObj.put("barcode", barCodeString)
        try {

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, jsonObj,
                Response.Listener { response ->
                    //TODO: Handle connection to database
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                }
            )
            NetworkManager.getInstance()?.addToRequestQueue(jsonObjectRequest)

        } catch (e: Throwable) {
            //TODO: Handle failed connection
        }

    }
}