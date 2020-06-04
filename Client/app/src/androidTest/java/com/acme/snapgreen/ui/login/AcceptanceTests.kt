package com.acme.snapgreen.ui.login


import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import com.acme.snapgreen.Constants
import com.acme.snapgreen.R
import com.acme.snapgreen.RealmUsageDataUtil
import com.acme.snapgreen.data.NetworkManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Acceptance tests for all major app features. Uses UI and requires a device.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class AcceptanceTests {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    /**
     * Asserts that given an incorrect product ID, server returns an error
     */
    @Test
    fun barcodeNegativeScanResultTest() {
        NetworkManager.getInstance(getInstrumentation().targetContext)

        val url = "${Constants.SERVER_URL}/products/900000014373"
        var success = false

        try {
            val jsonRequest = JsonObjectRequest(
                Request.Method.GET,
                url, null,
                Response.Listener { response ->
                    // Set the ingredients and the score in the ui
                },
                Response.ErrorListener {
                    success = true

                }
            )
            NetworkManager.getInstance()?.addToRequestQueue(jsonRequest)

        } catch (e: Throwable) {
            Log.e("AcceptanceTests", "Connection request failed")
        }
        Thread.sleep(2000)
        assertTrue(success)
    }

    /**
     * Asserts that given a valid product id,
     * server returns a json with the correct score and ingredients
     */
    @Test
    fun barcodePositiveScanResultTest() {
        NetworkManager.getInstance(getInstrumentation().targetContext)

        val url = "${Constants.SERVER_URL}/products/000000014373"
        var success = false

        try {
            val jsonRequest = JsonObjectRequest(
                Request.Method.GET,
                url, null,
                Response.Listener { response ->
                    // Set the ingredients and the score in the ui
                    assertEquals(response.get("score"), 0)
                    val ingredients = response.getJSONArray("ingredients")
                    assertEquals(ingredients[0], "refined olive oil")
                    assertEquals(ingredients[1], "extra virgin olive oil")
                    success = true
                },
                Response.ErrorListener {

                }
            )
            NetworkManager.getInstance()?.addToRequestQueue(jsonRequest)

        } catch (e: Throwable) {
            Log.e("AcceptanceTests", "Connection request failed")
        }
        Thread.sleep(1500)
        assertTrue(success)
    }

    /**
     * Asserts that on a clean install, the dashboard can be reached and displays the correct values
     */
    @Test
    fun cleanInstallDashboardTest() {
        RealmUsageDataUtil.setupRealm()
        loginWithTestUser()

        val scoreText = onView(
            allOf(
                withId(R.id.dashboard_grade),
                isDisplayed()
            )
        )
        scoreText.check(matches(withText("0")))

        val waterPercentText = onView(
            allOf(
                withId(
                    R.id.dashboard_water_percent
                ),
                isDisplayed()
            )
        )
        waterPercentText.check(matches(withText("N/A")))

        val trashPercentText = onView(
            allOf(
                withId(R.id.dashboard_trash_percent),
                isDisplayed()
            )
        )
        trashPercentText.check(matches(withText("N/A")))

        val dashboardTrashUsageText = onView(
            allOf(
                withId(R.id.dashboard_trash_usage),
                isDisplayed()
            )
        )
        dashboardTrashUsageText.check(matches(withText("0.0 kg")))

        val dashboardWaterUsageText = onView(
            allOf(
                withId(R.id.dashboard_water_usage),
                isDisplayed()
            )
        )
        dashboardWaterUsageText.check(matches(withText("0.0 gal")))
        RealmUsageDataUtil.breakdownRealm()
    }

    /**
     * Asserts that on a clean install, the usage input page works properly and sets the dashboard
     * statistics to the proper values.
     */
    @Test
    fun inputUsageTest() {
        RealmUsageDataUtil.setupRealm()
        loginWithTestUser()
        val appCompatButton4 = onView(
            allOf(withId(R.id.usage_input), withText("LOG USAGE"), isDisplayed())
        )
        appCompatButton4.perform(click())

        val appCompatEditText = onView(
            allOf(withId(R.id.minutesShowered), isDisplayed())
        )
        appCompatEditText.perform(replaceText("1"), closeSoftKeyboard())
        appCompatEditText.perform(pressImeActionButton())
        Thread.sleep(250)

        val appCompatEditText3 = onView(
            allOf(withId(R.id.timesFlushed), isDisplayed())
        )
        appCompatEditText3.perform(replaceText("2"), closeSoftKeyboard())
        appCompatEditText3.perform(pressImeActionButton())
        Thread.sleep(250)

        val appCompatEditText5 = onView(
            allOf(withId(R.id.timesDishwasherRun), isDisplayed())
        )
        appCompatEditText5.perform(replaceText("3"), closeSoftKeyboard())
        appCompatEditText5.perform(pressImeActionButton())
        Thread.sleep(250)

        val appCompatEditText7 = onView(
            allOf(withId(R.id.minutesWashingMachine), isDisplayed())
        )
        appCompatEditText7.perform(replaceText("4"), closeSoftKeyboard())
        appCompatEditText7.perform(pressImeActionButton())
        Thread.sleep(250)

        val appCompatEditText8 = onView(
            allOf(withId(R.id.numAlumCansUsed), isDisplayed())
        )
        appCompatEditText8.perform(replaceText("5"), closeSoftKeyboard())
        appCompatEditText8.perform(pressImeActionButton())
        Thread.sleep(250)

        val appCompatEditText9 = onView(
            allOf(withId(R.id.numStyroContainersUsed), isDisplayed())
        )
        appCompatEditText9.perform(replaceText("6"), closeSoftKeyboard())
        appCompatEditText9.perform(pressImeActionButton())
        Thread.sleep(250)

        val appCompatButton5 = onView(
            allOf(withId(R.id.editButton), withText("Save"), isDisplayed())
        )
        appCompatButton5.perform(click())
        Thread.sleep(1000)

        val textView = onView(
            allOf(withId(R.id.dashboard_grade), withText("3"), isDisplayed())
        )
        textView.check(matches(withText("3")))

        val textView2 = onView(
            allOf(withId(R.id.dashboard_water_usage), withText("31.3 gal"), isDisplayed())
        )
        textView2.check(matches(withText("31.3 gal")))
        RealmUsageDataUtil.breakdownRealm()
    }

    /**
     * Asserts that the test users friend is properly retrieved from the server and loaded to the UI
     */
    @Test
    fun populateFriendsListTest() {
        loginWithTestUser()
        val appCompatButton4 = onView(
            allOf(withId(R.id.invite), isDisplayed())
        )
        appCompatButton4.perform(click())

        val viewGroup = onView(withIndex(withId(R.id.linearLayout4), 0));

        Thread.sleep(1000)
        viewGroup.check(matches(isDisplayed()))

    }

    /**
     * Asserts that the test user can add a new user and have them displayed to the UI
     */
    @Test
    fun addUserFriendsListTest() {
        loginWithTestUser()
        val appCompatButton4 = onView(
            allOf(withId(R.id.invite), isDisplayed())
        )
        appCompatButton4.perform(click())

        val appCompatEditText = onView(
            allOf(withId(R.id.invite_user_text), isDisplayed())
        )
        appCompatEditText.perform(
            replaceText("destin.estrela@gmail.com"),
            closeSoftKeyboard()
        )
        appCompatEditText.perform(pressImeActionButton())
        Thread.sleep(1000)

        val viewGroup = onView(
            allOf(withId(R.id.linearLayout4), isDisplayed())
        )
        Thread.sleep(1000)

        isToastMessageDisplayed("Added destin.estrela@gmail.com")
    }

    /**
     * Asserts that signing out brings you properly back to the splash screen
     */
    @Test
    fun signOutTest() {
        loginWithTestUser()

        val appCompatButton4 = onView(
            allOf(withId(R.id.settings), isDisplayed())
        )
        appCompatButton4.perform(click())

        val appCompatButton5 = onView(
            allOf(withId(R.id.signOutSettingsButton), isDisplayed())
        )
        appCompatButton5.perform(click())

        val button = onView(
            allOf(withId(R.id.login_btn), isDisplayed())
        )
        button.check(matches(isDisplayed()))
    }

    /**
     * Asserts that given a weeks worth of specific usage input data, the feedback is properly
     * displayed for both water and waste
     */
    @Test
    fun statsPageTest() {
        loginWithTestUser()
        RealmUsageDataUtil.breakdownRealm()
        RealmUsageDataUtil.addWeeksData()

        val appCompatButton4 = onView(
            allOf(withId(R.id.stats), withText("Stats"), isDisplayed())
        )
        appCompatButton4.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.dailyFeedback),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Your water usage was greater than yesterday and you did worse than the daily average of 31 gal.\nLet's try to improve!")))

        val toggleButton = onView(
            allOf(withId(R.id.statsToggle), isDisplayed())
        )
        toggleButton.check(matches(isDisplayed()))

        val appCompatToggleButton = onView(
            allOf(withId(R.id.statsToggle), withText("Water"))
        )
        appCompatToggleButton.perform(scrollTo(), click())

        val textView2 = onView(
            allOf(
                withId(R.id.dailyFeedback),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Your waste output was less than yesterday ,however, you did worse than the daily average of 0.003 kg.\nLet's try to improving even more!")))
        RealmUsageDataUtil.breakdownRealm()
    }

    private fun loginWithTestUser() {
        val appCompatButton = onView(
            allOf(withId(R.id.login_btn), withText("Get Snappin'"), isDisplayed())
        )
        appCompatButton.perform(click())

        val supportVectorDrawablesButton = onView(
            allOf(withId(R.id.email_button), withText("Sign in with email"))
        )
        supportVectorDrawablesButton.perform(scrollTo(), click())

        val device = UiDevice.getInstance(getInstrumentation())
        Thread.sleep(100)
        device.pressBack()

        val textInputEditText = onView(
            withId(R.id.email)
        )
        textInputEditText.perform(
            scrollTo(),
            replaceText("bruno_da_professor@309.com"),
            closeSoftKeyboard()
        )

        val appCompatButton2 = onView(
            allOf(withId(R.id.button_next), withText("Next"))
        )
        appCompatButton2.perform(scrollTo(), click())
        Thread.sleep(100)

        val textInputEditText2 = onView(
            withId(R.id.password)
        )
        textInputEditText2.perform(scrollTo(), replaceText("123456"), closeSoftKeyboard())

        val appCompatButton3 = onView(
            allOf(withId(R.id.button_done), withText("Sign in"))
        )
        appCompatButton3.perform(scrollTo(), click())
        Thread.sleep(2000)
    }

    /** Asserts that the toast matches the parameter string*/
    private fun isToastMessageDisplayed(textId: String) {
        onView(withText(textId)).inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

}

/**
 * Allows asserting that a certain toast message appears
 */
class ToastMatcher : TypeSafeMatcher<Root?>() {
    override fun describeTo(description: org.hamcrest.Description?) {
        description?.appendText("is toast")
    }

    override fun matchesSafely(root: Root?): Boolean {
        val type: Int? = root?.windowLayoutParams?.get()?.type
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            val windowToken: IBinder = root.decorView.windowToken
            val appToken: IBinder = root.decorView.applicationWindowToken
            if (windowToken === appToken) {
                // windowToken == appToken means this window isn't contained by any other windows.
                // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                return true
            }
        }
        return false
    }
}

/**
 * Allows choosing between ambiguously named elements in a recycler or list view
 */
fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?>? {
    return object : TypeSafeMatcher<View?>() {
        var currentIndex = 0
        override fun describeTo(description: org.hamcrest.Description?) {
            description?.appendText("with index: ")
            description?.appendValue(index)
            matcher.describeTo(description)
        }

        override fun matchesSafely(view: View?): Boolean {
            return matcher.matches(view) && currentIndex++ == index
        }
    }
}