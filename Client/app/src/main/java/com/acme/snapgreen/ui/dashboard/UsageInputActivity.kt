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
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.DateFormat
import java.util.*


class UsageInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, "Activity launched!", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_usage_input)

        var currentDateField = findViewById<TextView>(R.id.currentDate)
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        currentDateField.setText(currentDateTimeString)

        var saysEdit = true
        var maxLength = 3
        var minutesShoweredField = findViewById<EditText>(R.id.minutesShowered)
        minutesShoweredField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))
        var timesFlushedField = findViewById<EditText>(R.id.timesFlushed)
        timesFlushedField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))
        var timesDishwaterRunField = findViewById<EditText>(R.id.timesDishwasherRun)
        timesDishwaterRunField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))
        var minutesWashingMachineField = findViewById<EditText>(R.id.minutesWashingMachine)
        minutesWashingMachineField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))

        var hoursLightOnField = findViewById<EditText>(R.id.hoursLightOn)
        hoursLightOnField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))
        var numAlumCansUsedField = findViewById<EditText>(R.id.numAlumCansUsed)
        numAlumCansUsedField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))
        var numStyroContainersUsedField = findViewById<EditText>(R.id.numStyroContainersUsed)
        numStyroContainersUsedField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))
        var numPlasticStrawsUsedField = findViewById<EditText>(R.id.numPlasticStrawsUsed)
        numPlasticStrawsUsedField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))
        var numPlasticUtensilsUsedField = findViewById<EditText>(R.id.numPlasticUtensilsUsed)
        numPlasticUtensilsUsedField.setFilters(arrayOf<InputFilter>(LengthFilter(maxLength)))

        var editButton = findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            if (saysEdit) {
                minutesShoweredField.setEnabled(true)
                timesFlushedField.setEnabled(true)
                timesDishwaterRunField.setEnabled(true)
                minutesWashingMachineField.setEnabled(true)

                hoursLightOnField.setEnabled(true)
                numAlumCansUsedField.setEnabled(true)
                numStyroContainersUsedField.setEnabled(true)
                numPlasticStrawsUsedField.setEnabled(true)
                numPlasticUtensilsUsedField.setEnabled(true)

                editButton.setText("SAVE")
                saysEdit = false
            }
            else {
                minutesShoweredField.setEnabled(false)
                timesFlushedField.setEnabled(false)
                timesDishwaterRunField.setEnabled(false)
                minutesWashingMachineField.setEnabled(false)

                hoursLightOnField.setEnabled(false)
                numAlumCansUsedField.setEnabled(false)
                numStyroContainersUsedField.setEnabled(false)
                numPlasticStrawsUsedField.setEnabled(false)
                numPlasticUtensilsUsedField.setEnabled(false)

                editButton.setText("EDIT")
                saysEdit = true
            }
        }

    }



}