package com.acme.snapgreen.ui.scanner

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.ui.dashboard.EXTRA_MESSAGE
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray


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

        var ingredientsArray : JSONArray
        val ingredients = findViewById<TextView>(R.id.barcode_response)
        val score = findViewById<TextView>(R.id.barcode_score)
        score.text = ""
        ingredients.text = ""

        //val url = "http://10.0.2.2:8080/products/$barCodeString"
        val url = "http://10.0.2.2:8080/products/12345" // for test purposes

        try {

            val jsonRequest = JsonObjectRequest(
                Request.Method.GET,
                url, null,
                Response.Listener { response ->
                    // Set the ingredients and the score in the ui
                    ingredientsArray = response.getJSONArray("ingredients")
                    score.text = "+" + response.getString("score")
                    for (i in 0 until ingredientsArray.length()) {
                        ingredients.append("\n" + ingredientsArray[i])
                    }
                },
                Response.ErrorListener {
                    // Do something when get error
                    ingredients.text = "Server error"
                    score.text = "No score sorry"
                }
            )
            NetworkManager.getInstance()?.addToRequestQueue(jsonRequest)

        } catch (e: Throwable) {
            //TODO: Handle failed connection
        }

    }
}