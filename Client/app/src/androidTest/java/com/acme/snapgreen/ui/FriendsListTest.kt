package com.acme.snapgreen.ui

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.acme.snapgreen.R
import com.acme.snapgreen.ui.login.LoginActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class FriendsListTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun inviteTest() {
        val appCompatButton = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.login_btn), ViewMatchers.withText("Get Snappin'"),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatButton.perform(ViewActions.click())

        val supportVectorDrawablesButton = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.email_button), ViewMatchers.withText("Sign in with email"),
                childAtPosition(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.btn_holder),
                        childAtPosition(
                            ViewMatchers.withId(R.id.container),
                            0
                        )
                    ),
                    0
                )
            )
        )
        supportVectorDrawablesButton.perform(ViewActions.scrollTo(), ViewActions.click())

        val textInputEditText = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.email),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(R.id.email_layout),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText.perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText("destin.estrela@gmail.com"),
            ViewActions.closeSoftKeyboard()
        )

        val appCompatButton2 = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.button_next), ViewMatchers.withText("Next"),
                childAtPosition(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.email_top_layout),
                        childAtPosition(
                            ViewMatchers.withClassName(Matchers.`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatButton2.perform(ViewActions.scrollTo(), ViewActions.click())

        val textInputEditText2 = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.password),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withId(R.id.password_layout),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText2.perform(
            ViewActions.scrollTo(),
            ViewActions.replaceText("Butter99"),
            ViewActions.closeSoftKeyboard()
        )

        val appCompatButton3 = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.button_done), ViewMatchers.withText("Sign in"),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withClassName(Matchers.`is`("android.widget.ScrollView")),
                        0
                    ),
                    4
                )
            )
        )
        appCompatButton3.perform(ViewActions.scrollTo(), ViewActions.click())

        val appCompatButton4 = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.invite)
            )
        )
        appCompatButton4.perform(ViewActions.click())
        SystemClock.sleep(1500);

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
