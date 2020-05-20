package com.acme.snapgreen

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.acme.snapgreen.data.DailyStatistic
import com.acme.snapgreen.data.StatUtil
import com.acme.snapgreen.data.WeeklyStatsCalc
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DateFormat
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

    @Test
    fun testCalcDailyStatistics() {
        // Context of the app under test.
        val testRealm = Realm.getDefaultInstance()
        for (i in 1..14) {
            addDSToRealm(i, 10, 4, 1, 50, 2, 1, 3, 4)
        }

        val combinedWS = WeeklyStatsCalc.getWeeksCombinedStats()

        assertEquals(933.8, combinedWS.numGals, .01)
        assertEquals(0.30982000000000004, combinedWS.numKgWaste, .01)
        assertEquals("933.800", "%.3f".format(combinedWS.numGals))
        assertEquals("0.310", "%.3f".format(combinedWS.numKgWaste))
    }

    @Test
    fun percentTest() {
        val testRealm: Realm = Realm.getDefaultInstance()

        for (i in 1..7) {
            addDSToRealm(i, 10, 4, 1, 50, 1, 1, 2, 1)
        }
        for (i in 8..14) {
            addDSToRealm(i, 15, 6, 2, 53, 1, 0, 1, 1)
        }

        val percentChanges = WeeklyStatsCalc.calculatePercentChange()

        assertEquals("↓ 16.15%", percentChanges.galsChange)
        assertEquals("↑ 27.51%", percentChanges.kgChange)
    }

    @Test
    fun testSetInputData() {

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
        assertEquals(-5, StatUtil.getScore().score)

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
        assertEquals(4, StatUtil.getScore().score)
    }

    @Test
    fun fillOneEmptyDayTest() {
        addDSToRealm(2, 10, 4, 1, 50, 0, 1, 3, 4)
        addDSToRealm(3, 2, 4, 1, 50, 5, 1, 3, 4)
        addDSToRealm(4, 1, 4, 1, 50, 3, 1, 3, 4)

        StatUtil.fillEmptyDays()
    }


    @Before
    public fun setupRealm() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.acme.snapgreen", appContext.packageName)

        Realm.init(appContext)
        val testConfig =
            RealmConfiguration.Builder().inMemory().name("test-realm").build()
        Realm.setDefaultConfiguration(testConfig)
    }

    @After
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
}
