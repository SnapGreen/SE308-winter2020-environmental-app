package com.acme.snapgreen.ui.scanner

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.ui.dashboard.EXTRA_MESSAGE
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scan_result.view.*
import org.w3c.dom.Text


/**
 * UI class for displaying the results of a bar code scan. We should show environmental impact
 * of the product as well as other applicable information.
 */
class ScanResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)


        val barCodeString = intent.getStringExtra(EXTRA_MESSAGE)
        val barcodeTextView = findViewById<TextView>(R.id.barcodeResult).apply {
            text = barCodeString
        }
        val barcodeResponseView = findViewById<TextView>(R.id.barcode_response)

        val url = "http://10.0.2.2:8080/products/$barCodeString"
        try {

            val stringRequest = StringRequest(
                Request.Method.GET,
                url,
                Response.Listener { response ->
                    // Do something with response string
                    barcodeResponseView.text = response
                },
                Response.ErrorListener {
                    // Do something when get error
                    barcodeResponseView.text = "Server error"
                }
            )
            NetworkManager.getInstance()?.addToRequestQueue(stringRequest)

        } catch (e: Throwable) {
            //TODO: Handle failed connection
        }

    }
}