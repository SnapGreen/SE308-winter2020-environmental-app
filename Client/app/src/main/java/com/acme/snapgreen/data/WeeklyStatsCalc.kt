package com.acme.snapgreen.data

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * A class to combine all the fields of data from the Usage Input page from the past 7 (or less if
 * there are less than 7 entries) days.
 * The combined data for any given day and for the whole week are stored in objects called
 * "CombinedDSData" and "CombinedWSData." (see bottom)
 */
class WeeklyStatsCalc() {

    companion object {

        /**
         * Getter method to access the list of DailyStatistics from past 7 days
         */
        fun getWeeklyStatsList(): List<DailyStatistic> {
            return StatUtil.getPastWeeksStats()
        }

        /**
         * Calculates the combined numbers for one day (based on that day's DailyStatistic) and returns
         * that information in a CombinedDSData object
         */
        private fun calculateDailyNumbers(ds: DailyStatistic): CombinedDSData {
            //piece of combined data to hold the number of gallons of water used in that day
            var numGals: Double =
                (ds.minutesShowered * 2.1) + (ds.timesFlushed * 1.6) + (ds.timesDishwasherRun * 6) + (ds.minutesWashingMachine * 2)
            numGals = BigDecimal(numGals).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            //piece of combined data to hold the number of kg of waste produced in that day
            var numKgWaste: Double =
                (ds.numAlumCansUsed * 0.00002) + (ds.numStyroContainersUsed * 0.6) + (ds.numPlasticStrawsUsed * 0.08) + (ds.numPlasticUtensilsUsed * 0.08)
            numKgWaste = BigDecimal(numKgWaste).setScale(2, RoundingMode.HALF_EVEN).toDouble()
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
            var weeklyDSList = getWeeklyStatsList()
            for (ds in weeklyDSList) {
                val combinedDaily: CombinedDSData = calculateDailyNumbers(ds)
                combinedWeekly.combinedDSDataList.add(combinedDaily)
                combinedWeekly.numGals += combinedDaily.numGals
                combinedWeekly.numKgWaste += combinedDaily.numKgWaste
            }

            return combinedWeekly
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
    }

}