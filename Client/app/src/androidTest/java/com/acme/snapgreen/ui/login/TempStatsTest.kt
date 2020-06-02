package com.acme.snapgreen.ui.login


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.acme.snapgreen.R
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class TempStatsTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun tempStatsTest() {
        val appCompatButton = onView(
            allOf(withId(R.id.login_btn), withText("Get Snappin'"), isDisplayed())
        )
        appCompatButton.perform(click())

        val supportVectorDrawablesButton = onView(
            allOf(withId(R.id.email_button), withText("Sign in with email"))
        )
        supportVectorDrawablesButton.perform(scrollTo(), click())

        val textInputEditText = onView(
            withId(R.id.email)
        )
        textInputEditText.perform(scrollTo(), replaceText("f@u.com"), closeSoftKeyboard())

        val appCompatButton2 = onView(
            allOf(withId(R.id.button_next), withText("Next"))
        )
        appCompatButton2.perform(scrollTo(), click())

        val textInputEditText2 = onView(
            withId(R.id.password)
        )
        textInputEditText2.perform(scrollTo(), replaceText("123456"), closeSoftKeyboard())

        val appCompatButton3 = onView(
            allOf(withId(R.id.button_done), withText("Sign in"))
        )
        appCompatButton3.perform(scrollTo(), click())

        val appCompatButton4 = onView(
            allOf(withId(R.id.stats), withText("Stats"), isDisplayed())
        )
        appCompatButton4.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.dailyFeedback),
                withText("Please enter data to receive feedback."),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Please enter data to receive feedback.")))

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
                withText("Please enter data to receive feedback."),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Please enter data to receive feedback.")))
    }
}
