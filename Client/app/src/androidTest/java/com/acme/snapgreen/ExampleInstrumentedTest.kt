package com.acme.snapgreen

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.acme.snapgreen.data.DailyStatistic
import com.acme.snapgreen.data.StatUtil
import com.acme.snapgreen.data.WeeklyStatsCalc
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private fun addDSToRealm(
        daysAgo: Int,
        testRealm: Realm,
        minsShowered: Int,
        timesFlushed: Int,
        timesDiswasher: Int,
        minsWashingMach: Int,
        numAlum: Int,
        numStyro: Int,
        numStraws: Int,
        numUtils: Int
    ): DailyStatistic {
        val testDS = DailyStatistic()
        testDS.date = Date(System.currentTimeMillis() - (daysAgo * 24) * 60 * 60 * 1000);
        testDS.minutesShowered = minsShowered
        testDS.timesFlushed = timesFlushed
        testDS.timesDishwasherRun = timesDiswasher
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

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.acme.snapgreen", appContext.packageName)

        Realm.init(appContext)
        val testConfig =
            RealmConfiguration.Builder().inMemory().name("test-realm").build()
        Realm.setDefaultConfiguration(testConfig)
        val testRealm: Realm = Realm.getInstance(testConfig)

        for (i in 1..14) {
            addDSToRealm(i, testRealm, 10, 4, 1, 50, 2, 1, 3, 4)
        }

        val combinedWS = WeeklyStatsCalc.getWeeksCombinedStats()

        assertEquals(133.4, combinedWS.numGals, .1)
        assertEquals(1.16004, combinedWS.numKgWaste, .1)
    }

    @Test
    fun testSetInputData() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.acme.snapgreen", appContext.packageName)

        Realm.init(appContext)
        val testConfig =
            RealmConfiguration.Builder().inMemory().name("test-realm").build()
        Realm.setDefaultConfiguration(testConfig)

        val stats = StatUtil.getTodaysStats()
        stats.minutesShowered = 12
        stats.timesFlushed = 4
        stats.timesDishwasherRun = 1
        stats.minutesWashingMachine = 30
        stats.numAlumCansUsed = 2
        stats.numStyroContainersUsed = 1
        stats.numPlasticStrawsUsed = 3
        stats.numPlasticUtensilsUsed = 5
        StatUtil.setTodaysStats(stats)
    }

    @Test
    fun testSetInputsTwiceToday() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.acme.snapgreen", appContext.packageName)

        Realm.init(appContext)
        val testConfig =
            RealmConfiguration.Builder().inMemory().name("test-realm").build()
        Realm.setDefaultConfiguration(testConfig)


        var stats = StatUtil.getTodaysStats()
        stats.date = Date(System.currentTimeMillis() - (0 * 24) * 60 * 60 * 1000);
        stats.minutesShowered = 10
        stats.timesFlushed = 4
        stats.timesDishwasherRun = 1
        stats.minutesWashingMachine = 50
        stats.numAlumCansUsed = 2
        stats.numStyroContainersUsed = 1
        stats.numPlasticStrawsUsed = 3
        stats.numPlasticUtensilsUsed = 4
        stats.barcodeScore = 0
        stats.hasBeenSaved = false
        StatUtil.setTodaysStats(stats)
        assertEquals(-5, StatUtil.getScore())

        stats = StatUtil.getTodaysStats()
        stats.minutesShowered = 5
        stats.timesFlushed = 9
        stats.timesDishwasherRun = 0
        stats.minutesWashingMachine = 27
        stats.numAlumCansUsed = 0
        stats.numStyroContainersUsed = 5
        stats.numPlasticStrawsUsed = 1
        stats.numPlasticUtensilsUsed = 0
        StatUtil.setTodaysStats(stats)
        assertEquals(4, StatUtil.getScore())
    }
}
