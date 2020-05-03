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
import com.acme.snapgreen.data.StatUtil
import java.text.DateFormat
import java.util.*


class UsageInputActivity : AppCompatActivity() {

    private fun saveScore(
        minutesShowered: Double,
        timesFlushed: Int,
        timesDishwasherRun: Int,
        minutesWashingMachine: Double,
        hoursLightOn: Double,
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
        stats.hoursLightOn = hoursLightOn
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
        Toast.makeText(applicationContext, "Activity launched!", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_usage_input)

        val currentDateField = findViewById<TextView>(R.id.currentDate)
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        currentDateField.text = currentDateTimeString

        var saysEdit = true
<<<<<<< HEAD
        var maxLength = 3
        var minutesShoweredField = findViewById<EditText>(R.id.minutesShowered)
        minutesShoweredField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        var timesFlushedField = findViewById<EditText>(R.id.timesFlushed)
        timesFlushedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        var timesDishwasherRunField = findViewById<EditText>(R.id.timesDishwasherRun)
        timesDishwasherRunField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        var minutesWashingMachineField = findViewById<EditText>(R.id.minutesWashingMachine)
        minutesWashingMachineField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))

        var hoursLightOnField = findViewById<EditText>(R.id.hoursLightOn)
        hoursLightOnField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        var numAlumCansUsedField = findViewById<EditText>(R.id.numAlumCansUsed)
        numAlumCansUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        var numStyroContainersUsedField = findViewById<EditText>(R.id.numStyroContainersUsed)
        numStyroContainersUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        var numPlasticStrawsUsedField = findViewById<EditText>(R.id.numPlasticStrawsUsed)
        numPlasticStrawsUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        var numPlasticUtensilsUsedField = findViewById<EditText>(R.id.numPlasticUtensilsUsed)
        numPlasticUtensilsUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))

        var editButton = findViewById<Button>(R.id.editButton)
=======
        val maxLength = 3
        val minutesShoweredField = findViewById<EditText>(R.id.minutesShowered)
        minutesShoweredField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val timesFlushedField = findViewById<EditText>(R.id.timesFlushed)
        timesFlushedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val timesDishwasherRunField = findViewById<EditText>(R.id.timesDishwasherRun)
        timesDishwasherRunField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val minutesWashingMachineField = findViewById<EditText>(R.id.minutesWashingMachine)
        minutesWashingMachineField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))

        val hoursLightOnField = findViewById<EditText>(R.id.hoursLightOn)
        hoursLightOnField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val numAlumCansUsedField = findViewById<EditText>(R.id.numAlumCansUsed)
        numAlumCansUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val numStyroContainersUsedField = findViewById<EditText>(R.id.numStyroContainersUsed)
        numStyroContainersUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val numPlasticStrawsUsedField = findViewById<EditText>(R.id.numPlasticStrawsUsed)
        numPlasticStrawsUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        val numPlasticUtensilsUsedField = findViewById<EditText>(R.id.numPlasticUtensilsUsed)
        numPlasticUtensilsUsedField.filters = arrayOf<InputFilter>(LengthFilter(maxLength))

        val editButton = findViewById<Button>(R.id.editButton)
>>>>>>> bb52b99e2fc093b53c82de927d1087f108467f20
        editButton.setOnClickListener {
            if (saysEdit) {
                minutesShoweredField.isEnabled = true
                timesFlushedField.isEnabled = true
                timesDishwasherRunField.isEnabled = true
                minutesWashingMachineField.isEnabled = true

                hoursLightOnField.isEnabled = true
                numAlumCansUsedField.isEnabled = true
                numStyroContainersUsedField.isEnabled = true
                numPlasticStrawsUsedField.isEnabled = true
                numPlasticUtensilsUsedField.isEnabled = true

                editButton.text = "SAVE"
                saysEdit = false
<<<<<<< HEAD
            }
            else {
=======
            } else {
>>>>>>> bb52b99e2fc093b53c82de927d1087f108467f20
                minutesShoweredField.isEnabled = false
                timesFlushedField.isEnabled = false
                timesDishwasherRunField.isEnabled = false
                minutesWashingMachineField.isEnabled = false

                hoursLightOnField.isEnabled = false
                numAlumCansUsedField.isEnabled = false
                numStyroContainersUsedField.isEnabled = false
                numPlasticStrawsUsedField.isEnabled = false
                numPlasticUtensilsUsedField.isEnabled = false

<<<<<<< HEAD
                saveScore(minutesShoweredField.text.toString().toDouble(), timesFlushedField.text.toString().toInt(), timesDishwasherRunField.text.toString().toInt(), minutesWashingMachineField.text.toString().toDouble(), hoursLightOnField.text.toString().toDouble(), numAlumCansUsedField.text.toString().toInt(), numStyroContainersUsedField.text.toString().toInt(), numPlasticStrawsUsedField.text.toString().toInt(), numPlasticUtensilsUsedField.text.toString().toInt())
=======
                saveScore(
                    minutesShoweredField.text.toString().toDouble(),
                    timesFlushedField.text.toString().toInt(),
                    timesDishwasherRunField.text.toString().toInt(),
                    minutesWashingMachineField.text.toString().toDouble(),
                    hoursLightOnField.text.toString().toDouble(),
                    numAlumCansUsedField.text.toString().toInt(),
                    numStyroContainersUsedField.text.toString().toInt(),
                    numPlasticStrawsUsedField.text.toString().toInt(),
                    numPlasticUtensilsUsedField.text.toString().toInt()
                )
>>>>>>> bb52b99e2fc093b53c82de927d1087f108467f20
                finish()

                editButton.text = "EDIT"
                saysEdit = true
            }
        }

    }


}