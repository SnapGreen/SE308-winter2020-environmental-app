package com.acme.snapgreen.ui.login


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
class LoginInputUsageTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun loginInputUsageTest() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.login_email),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("hello"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.login_email), withText("hello"),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(pressImeActionButton())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.password),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("password"), closeSoftKeyboard())

        val editText = onView(
            allOf(
                withId(R.id.login_email), withText("hello"),
                isDisplayed()
            )
        )
        editText.check(matches(withText("hello")))

        val editText2 = onView(
            allOf(
                withId(R.id.login_email), withText("hello"),
                childAtPosition(
                    allOf(
                        withId(R.id.test_button),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        editText2.check(matches(withText("hello")))

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.password), withText("password"),
                childAtPosition(
                    allOf(
                        withId(R.id.test_button),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(pressImeActionButton())

        val appCompatButton = onView(
            allOf(
                withId(R.id.usage_input), withText("LOG USAGE"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.editButton), withText("Edit"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.minutesShowered),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(replaceText("3"), closeSoftKeyboard())

        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.minutesShowered), withText("3"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText6.perform(pressImeActionButton())

        val appCompatEditText7 = onView(
            allOf(
                withId(R.id.timesFlushed),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText7.perform(replaceText("2"), closeSoftKeyboard())

        val appCompatEditText8 = onView(
            allOf(
                withId(R.id.timesFlushed), withText("2"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText8.perform(pressImeActionButton())

        val appCompatEditText9 = onView(
            allOf(
                withId(R.id.timesDishwasherRun),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        3
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText9.perform(replaceText("9"), closeSoftKeyboard())

        val appCompatEditText10 = onView(
            allOf(
                withId(R.id.timesDishwasherRun), withText("9"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        3
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText10.perform(pressImeActionButton())

        val appCompatEditText11 = onView(
            allOf(
                withId(R.id.minutesWashingMachine),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        4
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText11.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText12 = onView(
            allOf(
                withId(R.id.minutesWashingMachine), withText("1"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        4
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText12.perform(pressImeActionButton())

        val appCompatEditText13 = onView(
            allOf(
                withId(R.id.hoursLightOn),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText13.perform(replaceText("6"), closeSoftKeyboard())

        val appCompatEditText14 = onView(
            allOf(
                withId(R.id.hoursLightOn), withText("6"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText14.perform(pressImeActionButton())

        val appCompatEditText15 = onView(
            allOf(
                withId(R.id.numAlumCansUsed),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText15.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText16 = onView(
            allOf(
                withId(R.id.numAlumCansUsed), withText("1"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText16.perform(pressImeActionButton())

        val appCompatEditText17 = onView(
            allOf(
                withId(R.id.numStyroContainersUsed),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        3
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText17.perform(replaceText("8"), closeSoftKeyboard())

        val appCompatEditText18 = onView(
            allOf(
                withId(R.id.numStyroContainersUsed), withText("8"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        3
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText18.perform(pressImeActionButton())

        val appCompatEditText19 = onView(
            allOf(
                withId(R.id.numPlasticStrawsUsed),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        4
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText19.perform(replaceText("6"), closeSoftKeyboard())

        val appCompatEditText20 = onView(
            allOf(
                withId(R.id.numPlasticStrawsUsed), withText("6"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        4
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText20.perform(pressImeActionButton())

        val appCompatEditText21 = onView(
            allOf(
                withId(R.id.numPlasticUtensilsUsed),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        5
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText21.perform(replaceText("4"), closeSoftKeyboard())

        val appCompatEditText22 = onView(
            allOf(
                withId(R.id.numPlasticUtensilsUsed), withText("4"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        5
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText22.perform(pressImeActionButton())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.editButton), withText("SAVE"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())
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
