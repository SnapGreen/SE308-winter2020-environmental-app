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

    private fun addDSToRealm(daysAgo: Int, testRealm: Realm) {
        val testDS = DailyStatistic()
        testDS.date = Date(System.currentTimeMillis() - (daysAgo * 24) * 60 * 60 * 1000);
        testDS.minutesShowered = 10
        testDS.timesFlushed = 4
        testDS.timesDishwasherRun = 1
        testDS.minutesWashingMachine = 50
        testDS.numAlumCansUsed = 2
        testDS.numStyroContainersUsed = 1
        testDS.numPlasticStrawsUsed = 3
        testDS.numPlasticUtensilsUsed = 4
        testRealm.beginTransaction()
        testRealm.copyToRealmOrUpdate(testDS)
        testRealm.commitTransaction()
        Log.i(
            "Realm Database",
            "Updating statistics associated with test"
        )
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
            addDSToRealm(i, testRealm)
        }

        val combinedWS = WeeklyStatsCalc.getWeeksCombinedStats()

        assertEquals(133.4, combinedWS.numGals, .1)
        assertEquals(1.16004, combinedWS.numKgWaste, .1)
    }
}
