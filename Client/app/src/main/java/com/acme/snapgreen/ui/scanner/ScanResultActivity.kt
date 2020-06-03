package com.acme.snapgreen.ui.scanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acme.snapgreen.Constants.Companion.SERVER_URL
import com.acme.snapgreen.R
import com.acme.snapgreen.data.NetworkManager
import com.acme.snapgreen.data.StatUtil
import com.acme.snapgreen.ui.dashboard.EXTRA_MESSAGE
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject

/**
 * UI class for displaying the results of a bar code scan. We should show environmental impact
 * of the product as well as other applicable information.
 */
class ScanResultActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    /**
     * Saves the score associated with the barcode to the database
     */
    private fun saveScore(score: Int) {
        val stats = StatUtil.getTodaysStats()
        stats.barcodeScore += score
        StatUtil.setTodaysStats(stats)
        Toast.makeText(applicationContext, "Added $score to your daily score", Toast.LENGTH_SHORT)
            .show()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_result)
        viewManager = LinearLayoutManager(this)


        val barCodeString = intent.getStringExtra(EXTRA_MESSAGE)
        findViewById<TextView>(R.id.barcodeResult).apply {
            text = barCodeString
        }

        val score = findViewById<TextView>(R.id.barcode_score)
        val saveButton = findViewById<Button>(R.id.result_save_button)
        val cancelButton = findViewById<Button>(R.id.result_cancel_button)
        score.text = ""


        saveButton.setOnClickListener {
            saveScore(score.text.toString().toInt())
            finish()
        }
        cancelButton.setOnClickListener {
            finish()
        }

        val url = "$SERVER_URL/products/$barCodeString"
        queryResults(url, saveButton, score)

    }

    private fun queryResults(
        url: String,
        saveButton: Button,
        score: TextView
    ) {
        try {

            val jsonRequest = JsonObjectRequest(
                Request.Method.GET,
                url, null,
                Response.Listener { response ->
                    // Set the ingredients and the score in the ui
                    populateResults(response, saveButton, score)
                },
                Response.ErrorListener {
                    score.text = "Product not found"
                }
            )
            NetworkManager.getInstance()?.addToRequestQueue(jsonRequest)

        } catch (e: Throwable) {
            Toast.makeText(
                applicationContext,
                "Network request failed",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateResults(
        response: JSONObject,
        saveButton: Button,
        score: TextView
    ) {
        viewAdapter = IngredientAdapter(response.getJSONArray("ingredients"))
        recyclerView = findViewById<RecyclerView>(R.id.ingredients_recycle_view).apply {

            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = viewAdapter
            saveButton.isEnabled = true
            score.text = response.getString("score")
        }
    }

    /**
     * The adapter to display ingredients in the barcode result recycle view
     */
    class IngredientAdapter(private val ingredientList: JSONArray) :
        RecyclerView.Adapter<IngredientAdapter.IngredientEntryHolder>() {

        class IngredientEntryHolder(view: View, val ingredients: TextView) :
            RecyclerView.ViewHolder(view)

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): IngredientEntryHolder {
            // create a new view
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.ingredient_display, parent, false)

            val ingredientText = view.findViewById<TextView>(R.id.ingredient)

            return IngredientEntryHolder(view, ingredientText)
        }

        // Replace the contents of a view (invoked by the layout manager)
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: IngredientEntryHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.ingredients.text = ingredientList[position].toString()
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = ingredientList.length()
    }
}