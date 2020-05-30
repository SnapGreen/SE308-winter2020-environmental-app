package com.acme.snapgreen.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.StatUtil
import com.acme.snapgreen.data.WeeklyStatsCalc
import com.acme.snapgreen.ui.scanner.PreviewActivity

const val EXTRA_MESSAGE = "com.acme.snapgreen.MESSAGE"

class DashboardActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var waterUsageText: TextView
    private lateinit var wasteUsageText: TextView
    private lateinit var waterPercentText: TextView
    private lateinit var wastePercentText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        scoreText = findViewById<TextView>(R.id.dashboard_grade)
        waterUsageText = findViewById<TextView>(R.id.dashboard_water_usage)
        wasteUsageText = findViewById<TextView>(R.id.dashboard_trash_usage)
        waterPercentText = findViewById<TextView>(R.id.dashboard_water_percent)
        wastePercentText = findViewById<TextView>(R.id.dashboard_trash_percent)

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
    private fun updateDashboardStatistics() {
        val combinedWS = WeeklyStatsCalc.getWeeksCombinedStats()
        waterUsageText.text = "%.1f".format(combinedWS.numGals) + " gal"
        wasteUsageText.text = "%.1f".format(combinedWS.numKgWaste) + " kg"

        scoreText.text = StatUtil.getScore().score.toString()

        val percentChanges = WeeklyStatsCalc.calculatePercentChange()
        waterPercentText.text = percentChanges.galsChange
        waterPercentText.setTextColor(Color.parseColor(percentChanges.galsColor))
        wastePercentText.text = percentChanges.kgChange
        wastePercentText.setTextColor(Color.parseColor(percentChanges.kgColor))
    }

    public override fun onResume() {
        super.onResume()
        updateDashboardStatistics()
    }
}
