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
        val weeklyWaterAverage = 219
        val weeklyWasteAverage = 0.021
        if (stats.combinedDSDataList.isEmpty()) {
            dailyHeader.text = "No Data Available"
            weeklyHeader.text = "No Data Available"
            dailyFeedback.text = "Please enter data to receive feedback."
            weeklyFeedback.text = "Please enter data to receive feedback."
        } else {
            waterCharts(stats.combinedDSDataList)
            val dailyFeedback: TextView = findViewById(R.id.dailyFeedback)
            val weeklyFeedback: TextView = findViewById(R.id.weeklyFeedback)
            dailyWaterFeedback(stats.combinedDSDataList, dailyFeedback, weeklyWaterAverage / 7)
            weeklyWaterFeedback(stats.numGals, weeklyFeedback, weeklyWaterAverage)
            val toggle: ToggleButton = findViewById(R.id.statsToggle)
            toggle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    dailyHeader.text = "Today's Waste(kg)"
                    weeklyHeader.text = "This Week's Waste(kg)"
                    wasteCharts(stats.combinedDSDataList)
                    dailyWasteFeedback(
                        stats.combinedDSDataList,
                        dailyFeedback,
                        weeklyWasteAverage / 7
                    )
                    weeklyWasteFeedback(
                        stats.numKgWaste,
                        weeklyFeedback,
                        weeklyWasteAverage
                    )
                } else {
                    dailyHeader.text = "Today's Water Usage(gal)"
                    weeklyHeader.text = "This Week's Water Usage(gal)"
                    waterCharts(stats.combinedDSDataList)
                    dailyWaterFeedback(
                        stats.combinedDSDataList,
                        dailyFeedback,
                        weeklyWaterAverage / 7
                    )
                    weeklyWaterFeedback(
                        stats.numGals,
                        weeklyFeedback,
                        weeklyWaterAverage
                    )
                }
            }
        }
    }

    private fun dailyWaterFeedback(
        stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>,
        dailyFeedback: TextView,
        dailyWaterAverage: Int
    ) {
        if (stats[stats.size - 1].numGals > stats[stats.size - 2].numGals) {
            if (stats[stats.size - 1].numGals > dailyWaterAverage) {
                dailyFeedback.text =
                    "Your water usage was greater than yesterday and you did worse than the daily average of " +
                            dailyWaterAverage.toString() + " gal.\nLet's try to improve!"
            } else {
                dailyFeedback.text =
                    "Your water usage was greater than yesterday ,however, you did better than the daily average of " +
                            dailyWaterAverage.toString() + " gal.\nLet's see if you can beat yourself!"
            }
        } else {
            if (stats[stats.size - 1].numGals > dailyWaterAverage) {
                dailyFeedback.text =
                    "Your water usage was less than yesterday ,however, you did worse than the daily average of " +
                            dailyWaterAverage.toString() + " gal.\nLet's try to improving even more!"
            } else {
                dailyFeedback.text =
                    "Your water usage was less than yesterday and you did better than the daily average of " +
                            dailyWaterAverage.toString() + " gal.\nGreat job!"
            }
        }
    }

    private fun dailyWasteFeedback(
        stats: MutableList<WeeklyStatsCalc.Companion.CombinedDSData>,
        dailyFeedback: TextView,
        dailyWasteAverage: Double
    ) {
        if (stats[stats.size - 1].numKgWaste > stats[stats.size - 2].numKgWaste) {
            if (stats[stats.size - 1].numKgWaste > dailyWasteAverage) {
                dailyFeedback.text =
                    "Your waste output was greater than yesterday and you did worse than the daily average of " +
                            dailyWasteAverage.toString() + " kg.\nLet's try to improve!"
            } else {
                dailyFeedback.text =
                    "Your waste output was greater than yesterday ,however, you did better than the daily average of " +
                            dailyWasteAverage.toString() + " kg.\nLet's see if you can beat yourself!"
            }
        } else {
            if (stats[stats.size - 1].numKgWaste > dailyWasteAverage) {
                dailyFeedback.text =
                    "Your waste output was less than yesterday ,however, you did worse than the daily average of " +
                            dailyWasteAverage.toString() + " kg.\nLet's try to improving even more!"
            } else {
                dailyFeedback.text =
                    "Your waste output was less than yesterday and you did better than the daily average of " +
                            dailyWasteAverage.toString() + " kg.\nGreat job!"
            }
        }
    }

    private fun weeklyWaterFeedback(
        userGals: Double,
        weeklyFeedback: TextView,
        weeklyWaterAverage: Int
    ) {
        if (userGals > weeklyWaterAverage) {
            weeklyFeedback.text = "Your weekly water usage was greater than the average of " +
                    weeklyWaterAverage.toString() + " gal.\nLet's do better."
        } else {
            weeklyFeedback.text = "Your weekly water usage was less than the average of " +
                    weeklyWaterAverage.toString() + " gal.\nLet's keep it up!"
        }
    }

    private fun weeklyWasteFeedback(
        userKGs: Double,
        weeklyFeedback: TextView,
        weeklyWasteAverage: Double
    ) {
        if (userKGs > weeklyWasteAverage) {
            weeklyFeedback.text = "Your weekly waste output was greater than the average of " +
                    weeklyWasteAverage.toString() + " kg.\nLet's do better."
        } else {
            weeklyFeedback.text = "Your weekly waste output was less than the average of " +
                    weeklyWasteAverage.toString() + " kg.\nLet's keep it up!"
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