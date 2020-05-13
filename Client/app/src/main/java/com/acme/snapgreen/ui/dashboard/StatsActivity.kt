package com.acme.snapgreen.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        val chart1 = findViewById<CombinedChart>(R.id.chart1)
        chart1.setDrawBarShadow(false)
        chart1.setDrawGridBackground(false)
        chart1.axisRight.isEnabled = false

        val barData = ArrayList<BarEntry>()
        barData.add(BarEntry(0f, 4f))
        barData.add(BarEntry(1f, 2f))
        barData.add(BarEntry(2f, 7f))
        barData.add(BarEntry(3f, 5f))
        barData.add(BarEntry(4f, 3f))
        barData.add(BarEntry(5f, 5f))
        barData.add(BarEntry(6f, 6f))
        val bars = BarDataSet(barData, "Daily Data")
        bars.color = Color.rgb(31,175, 241)
        bars.setDrawValues(false)

        val lineData = ArrayList<Entry>()
        lineData.add(Entry(0f, 3f))
        lineData.add(Entry(1f, 4f))
        lineData.add(Entry(2f, 2f))
        lineData.add(Entry(3f, 4f))
        lineData.add(Entry(4f, 7f))
        lineData.add(Entry(5f, 1f))
        lineData.add(Entry(6f, 4f))
        val lines = LineDataSet(lineData, "Average Data")
        lines.color = Color.rgb(139, 195, 74)
        lines.setDrawValues(false)
        lines.setDrawFilled(true)
        lines.setDrawCircles(false)
        lines.setDrawCircleHole(false)

        val xLabel: ArrayList<String> = ArrayList()
        xLabel.add("Sun")
        xLabel.add("Mon")
        xLabel.add("Tues")
        xLabel.add("Wed")
        xLabel.add("Thurs")
        xLabel.add("Fri")
        xLabel.add("Sat")
        chart1.xAxis.valueFormatter = IndexAxisValueFormatter(xLabel)

        chart1.description.text = "Water Usage(gal)"
        val data = CombinedData()
        data.setData(BarData(bars))
        data.setData(LineData(lines))
        chart1.data = data
        chart1.setTouchEnabled(true)
        chart1.setPinchZoom(true)
        chart1.invalidate()
    }
}