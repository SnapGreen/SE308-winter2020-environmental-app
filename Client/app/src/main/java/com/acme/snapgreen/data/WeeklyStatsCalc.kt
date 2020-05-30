package com.acme.snapgreen.data

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.abs

/**
 * A class to combine all the fields of data from the Usage Input page from the past 7 (or less if
 * there are less than 7 entries) days.
 * The combined data for any given day and for the whole week are stored in objects called
 * "CombinedDSData" and "CombinedWSData." (see bottom)
 */
class WeeklyStatsCalc() {

    companion object {

        /**
         * Calculates the combined numbers for one day (based on that day's DailyStatistic) and returns
         * that information in a CombinedDSData object
         */
        private fun calculateDailyNumbers(ds: DailyStatistic): CombinedDSData {
            //piece of combined data to hold the number of gallons of water used in that day
            var numGals: Double =
                (ds.minutesShowered * 2.1) + (ds.timesFlushed * 1.6) + (ds.timesDishwasherRun * 6) + (ds.minutesWashingMachine * 2)
            //piece of combined data to hold the number of kg of waste produced in that day
            var numKgWaste: Double =
                (ds.numAlumCansUsed * 0.0149) + (ds.numStyroContainersUsed * 0.0044) + (ds.numPlasticStrawsUsed * 0.00042) + (ds.numPlasticUtensilsUsed * 0.0022)
            var date: Date =
                ds.date

            return CombinedDSData(date, numGals, numKgWaste)
        }

        /**
         * Calculates combined numbers for past 7 (or less) days (based on the past week's
         * DailyStatistics) and returns that information in a CombinedWSData object
         */
        fun getWeeksCombinedStats(): CombinedWSData {
            var combinedWeekly: CombinedWSData =
                CombinedWSData(0.0, 0.0, mutableListOf<CombinedDSData>())
            //extracts data from the past 7 days in a list from the StatUtil class
            var weeklyDSList: List<DailyStatistic> = StatUtil.getPastWeeksStats()

            combineWeekData(weeklyDSList, combinedWeekly)

            return combinedWeekly
        }

        /**
         * Calculates combined numbers for the week before the last (based on the List of
         * DailyStatistic objects passed in) and returns that information in a CombinedWSData
         * object. Used only to calculate the percentage change on Dashboard.
         */
        private fun getWeeksCombinedStats(weeklyDSList: MutableList<DailyStatistic>): CombinedWSData {
            var combinedWeekly: CombinedWSData =
                CombinedWSData(0.0, 0.0, mutableListOf<CombinedDSData>())

            combineWeekData(weeklyDSList, combinedWeekly)

            return combinedWeekly
        }

        private fun combineWeekData(
            weeklyDSList: List<DailyStatistic>,
            combinedWeekly: CombinedWSData
        ) {
            for (ds in weeklyDSList) {
                val combinedDaily: CombinedDSData = calculateDailyNumbers(ds)
                combinedWeekly.combinedDSDataList.add(combinedDaily)
                combinedWeekly.numGals += combinedDaily.numGals
                combinedWeekly.numKgWaste += combinedDaily.numKgWaste
            }
        }

        /**
         * Calculate percentage changes and returns it in a PercentChangeData object.
         */
        fun calculatePercentChange(): PercentChangeData {
            var weeklyDSList: MutableList<DailyStatistic> =
                StatUtil.getPastTwoWeeksStats().toMutableList()
            var percentChanges: PercentChangeData =
                PercentChangeData("", "", "", "")

            if (weeklyDSList.size <= 7) {
                percentChanges.galsChange = "N/A"
                percentChanges.kgChange = "N/A"
                percentChanges.galsColor = "#0099cc" // default to blue text
                percentChanges.kgColor = "#0099cc"   // default to blue text
                return percentChanges
            }

            for (i in 1..7) {
                weeklyDSList.removeAt(0)
            }

            var weeklyDataOld = getWeeksCombinedStats(weeklyDSList)
            var weeklyDataNew = getWeeksCombinedStats()
            var changeGals =
                (abs(weeklyDataOld.numGals - weeklyDataNew.numGals) / weeklyDataOld.numGals) * 100
            changeGals = BigDecimal(changeGals).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            var changeKg =
                (abs(weeklyDataOld.numKgWaste - weeklyDataNew.numKgWaste) / weeklyDataOld.numKgWaste) * 100
            changeKg = BigDecimal(changeKg).setScale(2, RoundingMode.HALF_EVEN).toDouble()

            if (weeklyDataOld.numGals > weeklyDataNew.numGals) {
                percentChanges.galsChange += "↓ "
                percentChanges.galsColor = "#cc0000"  // red text
            } else if (weeklyDataOld.numGals < weeklyDataNew.numGals) {
                percentChanges.galsChange += "↑ "
                percentChanges.galsColor = "#0099cc"  // blue text
            }
            if (weeklyDataOld.numKgWaste > weeklyDataNew.numKgWaste) {
                percentChanges.kgChange += "↓ "
                percentChanges.kgColor = "#cc0000"  // red text
            } else if (weeklyDataOld.numKgWaste < weeklyDataNew.numKgWaste) {
                percentChanges.kgChange += "↑ "
                percentChanges.kgColor = "#0099cc"  // blue text
            }

            percentChanges.galsChange += "$changeGals%"
            percentChanges.kgChange += "$changeKg%"

            return percentChanges
        }

        /**
         * Object to hold the combined data from any given day
         */
        class CombinedDSData(
            var date: Date,
            var numGals: Double,
            var numKgWaste: Double
        ) {
        }

        /**
         * Object to hold the combined data from the past week (could be less than past 7 days if
         * there are less than 7 entries)
         */
        class CombinedWSData(
            var numGals: Double,
            var numKgWaste: Double,
            var combinedDSDataList: MutableList<CombinedDSData>
        ) {
        }

        /**
         * Object to hold the combined data from any given day
         */
        class PercentChangeData(
            var galsChange: String,
            var galsColor: String,
            var kgChange: String,
            var kgColor: String
        ) {
        }
    }

}