package com.acme.snapgreen.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.acme.snapgreen.R
import com.acme.snapgreen.data.WeeklyStatsCalc
import com.acme.snapgreen.ui.scanner.PreviewActivity

const val EXTRA_MESSAGE = "com.acme.snapgreen.MESSAGE"

class DashboardActivity : AppCompatActivity() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var gradeText: TextView
    private lateinit var waterUsageText: TextView
    private lateinit var wasteUsageText: TextView
    private lateinit var waterPercentText: TextView
    private lateinit var wastePercentText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dashboardViewModel = ViewModelProviders.of(this, DashboardViewModelFactory())
            .get(DashboardViewModel::class.java)
        setContentView(R.layout.activity_dashboard)

        // get the username from the login activity
        val username = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val welcomeView = findViewById<TextView>(R.id.textView)

        val gradeText = findViewById<TextView>(R.id.dashboard_grade)
        waterUsageText = findViewById<TextView>(R.id.dashboard_water_usage)
        wasteUsageText = findViewById<TextView>(R.id.dashboard_trash_usage)
        val waterPercentText = findViewById<TextView>(R.id.dashboard_water_percent)
        val wastePercentText = findViewById<TextView>(R.id.dashboard_trash_percent)

        val waterButton = findViewById<Button>(R.id.usage_input)
        waterButton.setOnClickListener {
            val intent = Intent(this, UsageInputActivity::class.java)
            startActivity(intent)
        }

        val inviteButton = findViewById<Button>(R.id.invite)
        inviteButton.setOnClickListener {
            val intent = Intent(this, InviteActivity::class.java)
            startActivity(intent)
        }
        inviteButton.background = null

        val settingsButton = findViewById<Button>(R.id.settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        settingsButton.background = null

        val scannerButton = findViewById<Button>(R.id.scan)
        scannerButton.setOnClickListener {
            val intent = Intent(this, PreviewActivity::class.java)
            startActivity(intent)
        }

        val statsButton = findViewById<Button>(R.id.stats)
        statsButton.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        updateDashboardStatistics()
    }

    /**
     * Updates the water usage, waste usage, percent change from last week, and the overall
     * grade displayed in the center of the dashboard.
     */
    fun updateDashboardStatistics() {
        //TODO: Query / calculate the combined statistics of the last 7 days and update the
        // text views accordingly.
        val combinedWS = WeeklyStatsCalc.getWeeksCombinedStats()
        waterUsageText.text = combinedWS.numGals.toString() + " gal"
        wasteUsageText.text = combinedWS.numKgWaste.toString() + " kg"

//        if (combinedWS.numGals == 0.0 || combinedWS.numKgWaste == 0.0) {
//            gradeText.text = "F"
//        } else {
//            gradeText.text = "B+"
//        }
    }
}
