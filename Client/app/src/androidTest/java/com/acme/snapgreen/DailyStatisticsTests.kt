package com.acme.snapgreen

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.acme.snapgreen.data.StatUtil
import com.acme.snapgreen.data.WeeklyStatsCalc
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DailyStatisticsTests {

    @Test
    fun testCalcDailyStatistics() {
        // Context of the app under test.
        Realm.getDefaultInstance()
        for (i in 1..14) {
            RealmUsageDataUtil.addDSToRealm(i, 10, 4, 1, 50, 2, 1, 3, 4)
        }

        val combinedWS = WeeklyStatsCalc.getWeeksCombinedStats()

        assertEquals(933.8, combinedWS.numGals, .01)
        assertEquals(0.30982000000000004, combinedWS.numKgWaste, .01)
        assertEquals("933.800", "%.3f".format(combinedWS.numGals))
        assertEquals("0.310", "%.3f".format(combinedWS.numKgWaste))
    }

    @Test
    fun percentTest() {
        Realm.getDefaultInstance()

        for (i in 1..7) {
            RealmUsageDataUtil.addDSToRealm(i, 10, 4, 1, 50, 1, 1, 2, 1)
        }
        for (i in 8..14) {
            RealmUsageDataUtil.addDSToRealm(i, 15, 6, 2, 53, 1, 0, 1, 1)
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

    @Before
    fun setupRealm() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        Realm.init(appContext)
        val testConfig =
            RealmConfiguration.Builder().inMemory().name("test-realm").build()
        Realm.setDefaultConfiguration(testConfig)
    }

    @After
    fun breakdownRealm() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

}
