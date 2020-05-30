package com.acme.snapgreen.ui.dashboard

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.acme.snapgreen.R
import com.acme.snapgreen.data.DailyStatistic
import com.acme.snapgreen.data.StatUtil
import io.realm.Realm
import io.realm.kotlin.where
import java.text.DateFormat
import java.util.*


class UsageInputActivity : AppCompatActivity() {

    private fun saveScore(
        minutesShowered: Int,
        timesFlushed: Int,
        timesDishwasherRun: Int,
        minutesWashingMachine: Int,
        numAlumCansUsed: Int,
        numStyroContainersUsed: Int,
        numPlasticStrawsUsed: Int,
        numPlasticUtensilsUsed: Int
    ) {
        val stats = StatUtil.getTodaysStats()
        stats.minutesShowered = minutesShowered
        stats.timesFlushed = timesFlushed
        stats.timesDishwasherRun = timesDishwasherRun
        stats.minutesWashingMachine = minutesWashingMachine
        stats.numAlumCansUsed = numAlumCansUsed
        stats.numStyroContainersUsed = numStyroContainersUsed
        stats.numPlasticStrawsUsed = numPlasticStrawsUsed
        stats.numPlasticUtensilsUsed = numPlasticUtensilsUsed
        StatUtil.setTodaysStats(stats)
        Toast.makeText(applicationContext, "Saved daily usage input", Toast.LENGTH_SHORT)
            .show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_input)

        val currentDateField = findViewById<TextView>(R.id.currentDate)
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        currentDateField.text = currentDateTimeString

        val maxLength = 3
        val minutesShoweredField = findViewById<EditText>(R.id.minutesShowered)
        minutesShoweredField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val timesFlushedField = findViewById<EditText>(R.id.timesFlushed)
        timesFlushedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val timesDishwasherRunField = findViewById<EditText>(R.id.timesDishwasherRun)
        timesDishwasherRunField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val minutesWashingMachineField = findViewById<EditText>(R.id.minutesWashingMachine)
        minutesWashingMachineField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))

        val numAlumCansUsedField = findViewById<EditText>(R.id.numAlumCansUsed)
        numAlumCansUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val numStyroContainersUsedField = findViewById<EditText>(R.id.numStyroContainersUsed)
        numStyroContainersUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val numPlasticStrawsUsedField = findViewById<EditText>(R.id.numPlasticStrawsUsed)
        numPlasticStrawsUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val numPlasticUtensilsUsedField = findViewById<EditText>(R.id.numPlasticUtensilsUsed)
        numPlasticUtensilsUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))

//        val dateStringList = DateFormat.getDateTimeInstance().format(Date()).split(",", " ")
//        val dateString = dateStringList[0] + " " + dateStringList[1] + " " + dateStringList[3]
//        val realm = Realm.getDefaultInstance()
//        var stat = realm.where<DailyStatistic>().contains("today", dateString).findFirst()
//        if (stat != null) {
//            minutesShoweredField.setHint(stat.minutesShowered)
//            timesFlushedField.setHint(stat.timesFlushed)
//            timesDishwasherRunField.setHint(stat.timesDishwasherRun)
//            minutesWashingMachineField.setHint(stat.minutesWashingMachine)
//            numAlumCansUsedField.setHint(stat.numAlumCansUsed)
//            numStyroContainersUsedField.setHint(stat.numStyroContainersUsed)
//            numPlasticStrawsUsedField.setHint(stat.numPlasticStrawsUsed)
//            numPlasticUtensilsUsedField.setHint(stat.numPlasticUtensilsUsed)
//        }

        val editButton = findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            var minutesShowered = if (minutesShoweredField.text.toString().isEmpty()) {
                0
            } else {
                minutesShoweredField.text.toString().toInt()
            }

            var timesFlushed = if (timesFlushedField.text.toString().isEmpty()) {
                0
            } else {
                timesFlushedField.text.toString().toInt()
            }

            var timesDishwasherRun = if (timesDishwasherRunField.text.toString().isEmpty()) {
                0
            } else {
                timesDishwasherRunField.text.toString().toInt()
            }

            var minutesWashingMachine = if (minutesWashingMachineField.text.toString().isEmpty()) {
                0
            } else {
                minutesWashingMachineField.text.toString().toInt()
            }

            var numAlumCansUsed = if (numAlumCansUsedField.text.toString().isEmpty()) {
                0
            } else {
                numAlumCansUsedField.text.toString().toInt()
            }

            var numStyroContainersUsed =
                if (numStyroContainersUsedField.text.toString().isEmpty()) {
                    0
                } else {
                    numStyroContainersUsedField.text.toString().toInt()
                }

            var numPlasticStrawsUsed = if (numPlasticStrawsUsedField.text.toString().isEmpty()) {
                0
            } else {
                numPlasticStrawsUsedField.text.toString().toInt()
            }

            var numPlasticUtensilsUsed =
                if (numPlasticUtensilsUsedField.text.toString().isEmpty()) {
                    0
                } else {
                    numPlasticUtensilsUsedField.text.toString().toInt()
                }

            saveScore(
                minutesShowered,
                timesFlushed,
                timesDishwasherRun,
                minutesWashingMachine,
                numAlumCansUsed,
                numStyroContainersUsed,
                numPlasticStrawsUsed,
                numPlasticUtensilsUsed
            )
            finish()
        }
    }

}
