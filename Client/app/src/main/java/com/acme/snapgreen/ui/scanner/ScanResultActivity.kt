package com.acme.snapgreen.ui.scanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.Constants.Companion.SERVER_URL
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.data.StatUtil
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

    /**
     * Saves the score associated with the barcode to the database
     */
    private fun saveScore(score: Int)
    {
        val stats = StatUtil.getTodaysStats()
        stats.score += score
        StatUtil.setTodaysStats(stats)
        Toast.makeText(applicationContext,"Added $score to your daily score", Toast.LENGTH_SHORT)
            .show()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)


        val barCodeString = intent.getStringExtra(EXTRA_MESSAGE)
        val barcodeTextView = findViewById<TextView>(R.id.barcodeResult).apply {
            text = barCodeString
        }

        var ingredientsArray: JSONArray
        val ingredients = findViewById<TextView>(R.id.barcode_response)
        val score = findViewById<TextView>(R.id.barcode_score)
        val saveButton = findViewById<Button>(R.id.result_save_button)
        val cancelButton = findViewById<Button>(R.id.result_cancel_button)
        score.text = ""
        ingredients.text = ""


        saveButton.setOnClickListener {
            saveScore(score.text.toString().toInt())
            finish()
        }
        cancelButton.setOnClickListener {
            finish()
        }

        val url = "$SERVER_URL/products/12345" // for test purposes

        try {

            val jsonRequest = JsonObjectRequest(
                Request.Method.GET,
                url, null,
                Response.Listener { response ->
                    // Set the ingredients and the score in the ui
                    ingredientsArray = response.getJSONArray("ingredients")
                    score.text = "+" + response.getString("score")
                    for (i in 0 until ingredientsArray.length().coerceAtMost(5)) {
                        ingredients.append("\n" + ingredientsArray[i])
                    }
                },
                Response.ErrorListener {

                }
            )
            NetworkManager.getInstance()?.addToRequestQueue(jsonRequest)

        } catch (e: Throwable) {
            //TODO: Handle failed connection
        }

    }
}