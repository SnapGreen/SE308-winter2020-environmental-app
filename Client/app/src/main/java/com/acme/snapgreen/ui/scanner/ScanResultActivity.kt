package com.acme.snapgreen.ui.scanner

import com.acme.snapgreen.data.DailyStatistic
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.ui.dashboard.EXTRA_MESSAGE
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import io.realm.Realm
import org.json.JSONArray
import java.text.DateFormat
import java.util.*
import io.realm.kotlin.where

/**
 * UI class for displaying the results of a bar code scan. We should show environmental impact
 * of the product as well as other applicable information.
 */
class ScanResultActivity : AppCompatActivity() {

    private fun saveScore(score: Int)
    {
        // get date and filter out time, only want month, day, year
        val dateStringList =
            DateFormat.getDateTimeInstance().format(Date()).split(",", " ")
        val dateString = dateStringList[0] + " " + dateStringList[1] + " " + dateStringList[3]
        val realm = Realm.getDefaultInstance()

        var stat = realm.where<DailyStatistic>().
        contains("today",  dateString).findFirst()
        realm.beginTransaction()

        if(stat == null) {
            // create entry for today's date if it does not exist
            stat = DailyStatistic().apply {
                this.score = score
                this.today = dateString
            }
            realm.copyToRealm(stat)
            Log.i("Realm Database",
                "Created " + dateString + " with a starting score of " + stat.score)
        }
        else {
            // update the score and update the database if it does
            stat.score += score
            realm.copyToRealmOrUpdate(stat)

            Log.i("Realm Database",
                "Updated " + dateString + "'s score to " + stat.score)

        }
        realm.commitTransaction()

    }

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
        score.text = "3"
        ingredients.text = ""


        saveButton.setOnClickListener {
            saveScore(score.text.toString().toInt())
        }


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