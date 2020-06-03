package com.acme.snapgreen

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.acme.snapgreen.data.DailyStatistic
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.Assert
import java.text.DateFormat
import java.util.*

class RealmUsageDataUtil {

    companion object {

        public fun setupRealm() {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            Assert.assertEquals("com.acme.snapgreen", appContext.packageName)

            Realm.init(appContext)
            val testConfig =
                RealmConfiguration.Builder().inMemory().name("test-realm").build()
            Realm.setDefaultConfiguration(testConfig)
        }

        public fun breakdownRealm() {
            var realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.deleteAll()
            realm.commitTransaction()
        }

        fun addWeeksData() {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            Realm.init(appContext)
            var realm = Realm.getDefaultInstance()

            addDSToRealm(7, 10, 4, 1, 50, 0, 1, 3, 4)
            addDSToRealm(6, 2, 4, 1, 50, 5, 1, 3, 4)
            addDSToRealm(5, 1, 4, 1, 50, 3, 1, 3, 4)
            addDSToRealm(4, 3, 4, 1, 50, 10, 1, 3, 4)
            addDSToRealm(3, 5, 4, 1, 50, 15, 1, 3, 4)
            addDSToRealm(2, 6, 4, 1, 50, 2, 1, 3, 4)
            addDSToRealm(1, 2, 4, 1, 50, 8, 1, 3, 4)

        }

        fun addDSToRealm(
            daysAgo: Int,
            minsShowered: Int,
            timesFlushed: Int,
            timesDishwasher: Int,
            minsWashingMach: Int,
            numAlum: Int,
            numStyro: Int,
            numStraws: Int,
            numUtils: Int
        ): DailyStatistic {
            val testRealm: Realm = Realm.getDefaultInstance()

            val testDS = DailyStatistic()
            testDS.date = Date(System.currentTimeMillis() - (daysAgo * 24) * 60 * 60 * 1000)
            testDS.today = DateFormat.getDateTimeInstance().format(testDS.date)
            testDS.minutesShowered = minsShowered
            testDS.timesFlushed = timesFlushed
            testDS.timesDishwasherRun = timesDishwasher
            testDS.minutesWashingMachine = minsWashingMach
            testDS.numAlumCansUsed = numAlum
            testDS.numStyroContainersUsed = numStyro
            testDS.numPlasticStrawsUsed = numStraws
            testDS.numPlasticUtensilsUsed = numUtils
            testDS.barcodeScore = 0
            testDS.hasBeenSaved = false
            testRealm.beginTransaction()
            testRealm.copyToRealmOrUpdate(testDS)
            testRealm.commitTransaction()
            Log.i(
                "Realm Database",
                "Updating statistics associated with test"
            )
            return testDS
        }
    }
}