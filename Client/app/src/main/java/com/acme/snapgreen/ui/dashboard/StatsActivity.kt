package com.acme.snapgreen.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val lineGraph = AnyChart.line()
        lineGraph.title("Total Water Used Over the Past Week (gallons)")
        lineGraph.yAxis(0).title("Gallons")
        lineGraph.xAxis(0).title("Days")
        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Mon", 3))
        data.add(ValueDataEntry("Tues", 5))
        data.add(ValueDataEntry("Wed", 8))
        data.add(ValueDataEntry("Thurs", 4))
        data.add(ValueDataEntry("Fri", 3))
        data.add(ValueDataEntry("Sat", 6))
        data.add(ValueDataEntry("Sun", 10))
        lineGraph.data(data)
        val anyChartView = findViewById<AnyChartView>(R.id.any_chart_view)
        anyChartView.setChart(lineGraph)
    }
}