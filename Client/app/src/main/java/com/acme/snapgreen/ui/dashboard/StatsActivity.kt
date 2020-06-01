package com.acme.snapgreen.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.WeeklyStatsCalc
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        val currentDateField = findViewById<TextView>(R.id.currentDate2)
        val currentDateTimeString = DateFormat.getDateInstance().format(Date())
        currentDateField.text = currentDateTimeString
        val stats = WeeklyStatsCalc.getWeeksCombinedStats()
        val dailyHeader: TextView = findViewById(R.id.dailyHeader)
        val weeklyHeader: TextView = findViewById(R.id.weeklyHeader)
        val dailyFeedback: TextView = findViewById(R.id.dailyFeedback)
        val weeklyFeedback: TextView = findViewById(R.id.weeklyFeedback)
        if (stats.combinedDSDataList.isEmpty()) {
            dailyHeader.text = "No Data Available"
            weeklyHeader.text = "No Data Available"
            dailyFeedback.text = "Please enter data to receive feedback."
            weeklyFeedback.text = "Please enter data to receive feedback."
        } else {
            waterCharts(stats.combinedDSDataList)
            val dailyFeedback: TextView = findViewById(R.id.dailyFeedback)
            val weeklyFeedback: TextView = findViewById(R.id.weeklyFeedback)
            dailyWaterFeedback(stats.combinedDSDataList, dailyFeedback)
            weeklyWaterFeedback(stats.combinedDSDataList, weeklyFeedback)
            val toggle: ToggleButton = findViewById(R.id.statsToggle)
            toggle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    dailyHeader.text = "Today's Waste(kg)"
                    weeklyHeader.text = "This Week's Waste(kg)"
                    wasteCharts(stats.combinedDSDataList)
                    dailyWasteFeedback(stats.combinedDSDataList, dailyFeedback)
                    weeklyWasteFeedback(stats.combinedDSDataList, weeklyFeedback)
                } else {
                    dailyHeader.text = "Today's Water Usage(gal)"
                    weeklyHeader.text = "This Week's Water Usage(gal)"
                    waterCharts(stats.combinedDSDataList)
                    dailyWaterFeedback(stats.combinedDSDataList, dailyFeedback)
                    weeklyWaterFeedback(stats.combinedDSDataList, weeklyFeedback)
                }
            }
        }
    }

    private fun dailyWaterFeedback(
        stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>,
        dailyFeedback: TextView
    ) {
        if (stats.size > 1 && stats[stats.size - 1].numGals > stats[stats.size - 2].numGals) {

        }
    }

    private fun dailyWasteFeedback(
        stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>,
        dailyFeedback: TextView
    ) {
        if (stats.size > 1 && stats[stats.size - 1].numGals > stats[stats.size - 2].numGals) {

        }
    }

    private fun weeklyWaterFeedback(
        stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>,
        dailyFeedback: TextView
    ) {
        if (stats.size > 1 && stats[stats.size - 1].numGals > stats[stats.size - 2].numGals) {

        }
    }

    private fun weeklyWasteFeedback(
        stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>,
        dailyFeedback: TextView
    ) {
        if (stats.size > 1 && stats[stats.size - 1].numGals > stats[stats.size - 2].numGals) {

        }
    }

    private fun waterCharts(stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>) {
        dailyWater(stats.last())
        weeklyWater(stats)
    }

    private fun dailyWater(today: WeeklyStatsCalc.Companion.CombinedDSData) {
        val dailyWaterChart = findViewById<HorizontalBarChart>(R.id.dailyChart)
        dailyProperties(dailyWaterChart)
        dailyWaterData(dailyWaterChart, today)
    }

    private fun dailyProperties(dailyChart: HorizontalBarChart) {
        dailyChart.description.isEnabled = false
        dailyChart.setDrawBarShadow(false)
        dailyChart.setDrawGridBackground(false)
        dailyChart.legend.isEnabled = false
        dailyChart.xAxis.setDrawGridLines(false)
        dailyChart.xAxis.setDrawLabels(false)
        dailyChart.axisLeft.setDrawLabels(false)
        dailyChart.axisLeft.axisMinimum = 0F
        dailyChart.axisRight.axisMinimum = 0F
        dailyChart.invalidate()
    }

    private fun dailyWaterData(
        dailyWaterChart: HorizontalBarChart,
        today: WeeklyStatsCalc.Companion.CombinedDSData
    ) {
        val data = ArrayList<BarEntry>()
        data.add(BarEntry(0f, today.numGals.toFloat()))
        val dailyWaterSet = BarDataSet(data, "Daily Water Use")
        dailyWaterSet.color = Color.rgb(31, 175, 241)
        val dailyWaterData = BarData(dailyWaterSet)
        dailyWaterChart.data = dailyWaterData
    }

    private fun weeklyWater(stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>) {
        val weeklyWaterChart = findViewById<CombinedChart>(R.id.weeklyChart)
        weeklyProperties(weeklyWaterChart, stats)
        weeklyWaterData(weeklyWaterChart, stats)
    }

    private fun weeklyProperties(
        weeklyChart: CombinedChart,
        days: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>
    ) {
        weeklyChart.setDrawBarShadow(false)
        weeklyChart.setDrawGridBackground(false)
        weeklyChart.axisRight.isEnabled = false
        val xLabel: ArrayList<String> = ArrayList()
        for (day in days) {
            xLabel.add(day.date.toString().subSequence(0, 3).toString())
        }
        weeklyChart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabel)
        weeklyChart.setTouchEnabled(true)
        weeklyChart.setPinchZoom(true)
        weeklyChart.invalidate()
        weeklyChart.description.isEnabled = false
    }

    private fun weeklyWaterData(
        weeklyWaterChart: CombinedChart,
        stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>
    ) {
        val barData = ArrayList<BarEntry>()
        var count = 0f
        for (stat in stats) {
            barData.add(BarEntry(count, stat.numGals.toFloat()))
            count++
        }
        val bars = BarDataSet(barData, "Daily Data")
        bars.color = Color.rgb(31, 175, 241)
        bars.setDrawValues(false)

        val lineData = ArrayList<Entry>()
        var average: Float
        var waterCount = 0f
        var dayCount2 = 0f
        for (stat in stats) {
            waterCount += stat.numGals.toFloat()
            dayCount2++
            average = (waterCount / dayCount2)
            lineData.add(Entry((dayCount2 - 1), average))
        }
        val lines = LineDataSet(lineData, "Average Data(past week)")
        lines.color = Color.rgb(139, 195, 74)
        lines.setDrawValues(false)
        lines.setDrawFilled(true)
        lines.setDrawCircles(false)
        lines.setDrawCircleHole(false)

        val weeklyWaterData = CombinedData()
        weeklyWaterData.setData(BarData(bars))
        weeklyWaterData.setData(LineData(lines))
        weeklyWaterChart.data = weeklyWaterData
    }

    private fun wasteCharts(stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>) {
        dailyWaste(stats.last())
        weeklyWaste(stats)
    }

    private fun dailyWaste(today: WeeklyStatsCalc.Companion.CombinedDSData) {
        val dailyWasteChart = findViewById<HorizontalBarChart>(R.id.dailyChart)
        dailyProperties(dailyWasteChart)
        dailyWasteData(dailyWasteChart, today)
    }

    private fun dailyWasteData(
        dailyWasteChart: HorizontalBarChart,
        today: WeeklyStatsCalc.Companion.CombinedDSData
    ) {
        val data = ArrayList<BarEntry>()
        data.add(BarEntry(0f, today.numKgWaste.toFloat()))
        val dailyWasteSet = BarDataSet(data, "Daily Waste")
        dailyWasteSet.color = Color.rgb(31, 175, 241)
        val dailyWasteData = BarData(dailyWasteSet)
        dailyWasteChart.data = dailyWasteData
    }

    private fun weeklyWaste(stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>) {
        val weeklyWasteChart = findViewById<CombinedChart>(R.id.weeklyChart)
        weeklyProperties(weeklyWasteChart, stats)
        weeklyWasteData(weeklyWasteChart, stats)
    }

    private fun weeklyWasteData(
        weeklyWasteChart: CombinedChart,
        stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>
    ) {
        val barData = ArrayList<BarEntry>()
        var dayCount = 0f
        for (stat in stats) {
            barData.add(BarEntry(dayCount, stat.numKgWaste.toFloat()))
            dayCount++
        }
        val bars = BarDataSet(barData, "Daily Data")
        bars.color = Color.rgb(31, 175, 241)
        bars.setDrawValues(false)

        val lineData = ArrayList<Entry>()
        var average: Float
        var wasteCount = 0f
        var dayCount2 = 0f
        for (stat in stats) {
            wasteCount += stat.numKgWaste.toFloat()
            dayCount2++
            average = (wasteCount / dayCount2)
            lineData.add(Entry((dayCount2 - 1), average))
        }
        val lines = LineDataSet(lineData, "Average Data(past week)")
        lines.color = Color.rgb(139, 195, 74)
        lines.setDrawValues(false)
        lines.setDrawFilled(true)
        lines.setDrawCircles(false)
        lines.setDrawCircleHole(false)

        val weeklyWasteData = CombinedData()
        weeklyWasteData.setData(BarData(bars))
        weeklyWasteData.setData(LineData(lines))
        weeklyWasteChart.data = weeklyWasteData
    }
}