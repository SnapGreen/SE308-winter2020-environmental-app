package com.acme.snapgreen.data

import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import java.text.DateFormat
import java.util.*

/**
 * A utility class to abstract the database component of updating daily usage.
 * Allows caller to get today's DailyStatistic and update its values
 */
@Suppress("SpellCheckingInspection")
class StatUtil private constructor() {

    companion object {

        /**
         * Returns the DailyStatistic associated with today's date from Realm DB, or creates one if
         * it doesn't exist.
         */
        fun getTodaysStats(): DailyStatistic {

            // get the date and parse out the specific time (we just want day/month/year)
            val dateStringList = DateFormat.getDateTimeInstance().format(Date()).split(",", " ")
            val dateString = dateStringList[0] + " " + dateStringList[1] + " " + dateStringList[3]
            val realm = Realm.getDefaultInstance()

            // query database for today's date to see if we've already created a DailyStat for today
            var stat = realm.where<DailyStatistic>().contains("today", dateString).findFirst()
            realm.beginTransaction()

            if (stat == null) {
                // create entry for today's date if it does not exist
                stat = DailyStatistic().apply {
                    this.today = dateString
                }
                realm.copyToRealm(stat)
                Log.i(
                    "Realm Database",
                    "Created DailyStatistic for $dateString"
                )
            } else {
                Log.i(
                    "Realm Database",
                    "Returning previously created DailyStatistic for $dateString"
                )
            }

            return stat
        }

        /**
         * Return a list of DailyStatistics for the past 7 days (or less if there are less than 7
         * total entries in the database)
         */
        fun getPastWeeksStats(): List<DailyStatistic> {
            val realm = Realm.getDefaultInstance()

            // query database for the DailyStatistic entries of the past 7 days
            return realm.where<DailyStatistic>().sort("date", Sort.DESCENDING).limit(7).findAll()
        }

        /**
         * Updates the DailyStatistic in the database
         */
        fun setTodaysStats(stats: DailyStatistic) {
            val realm = Realm.getDefaultInstance()
            realm.copyToRealmOrUpdate(stats)
            realm.commitTransaction()
            Log.i(
                "Realm Database",
                "Updating statistics associated with ${stats.today}"
            )
        }
    }

}