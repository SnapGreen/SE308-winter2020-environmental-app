package com.acme.snapgreen.ui.stats


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.acme.snapgreen.R
import com.acme.snapgreen.ui.login.LoginActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class StatsActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun statsActivityTest() {
        val button = onView(
            allOf(
                withId(R.id.login),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val button2 = onView(
            allOf(
                withId(R.id.login),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        val button3 = onView(
            allOf(
                withId(R.id.login),
                isDisplayed()
            )
        )
        button3.check(matches(isDisplayed()))

        val appCompatEditText = onView(
            allOf(
                withId(R.id.login_email),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("JSHUNDAL"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.password),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("JDJDJdscd"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.password), withText("JDJDJdscd"),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(pressImeActionButton())

        val appCompatButton = onView(
            allOf(
                withId(R.id.stats), withText("Stats"),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
