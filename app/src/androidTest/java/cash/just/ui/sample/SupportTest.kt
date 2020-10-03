package cash.just.ui.sample


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import cash.just.support.BaseSupportPage
import cash.just.support.pages.GeneralSupportPage
import junit.framework.AssertionFailedError
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class SupportTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)


    @Test
    fun testEnterList() {
       var i:Int=0;
        BaseSupportPage.allPages().forEach {

            val textViewTitle =  onView(withText(it.title()))
            textViewTitle.perform(click())

            onView(withId(R.id.supportPageTitle)).check(matches(withText(it.title())))
            onView(withId(R.id.supportPageDescription)).check(matches(withText(it.description())))
            onView(withId(R.id.faqImage))
                .perform(click())
            try {
                textViewTitle.check(matches(isDisplayed()))
                textViewTitle.perform(swipeUp())
                textViewTitle.check(matches(isDisplayed()))
                textViewTitle.perform(swipeUp())
            } catch (e: AssertionFailedError) {
                // View not displayed
            } catch (e: PerformException) {
                // View not displayed
            }

        }
    }

    //@Test
    fun supportTest() {


        val button = onView(
            allOf(
                withText("INDEX"),
                childAtPosition(
                    allOf(
                        withId(R.id.rootView),
                        childAtPosition(
                            withClassName(`is`("android.widget.ScrollView")),
                            0
                        )
                    ),
                    4
                )
            )
        )
        button.perform(scrollTo(), click())



        enterFAQ(
            GeneralSupportPage.GET_STARTED.title(),
            GeneralSupportPage.GET_STARTED.description(),
            0
        );
        enterFAQ(GeneralSupportPage.SEND.title(), GeneralSupportPage.SEND.description(), 1);

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

    private fun enterFAQ(title: String, description: String, position: Int) {
        enterFAQ(title, title, description, position)
    }

    private fun enterFAQ(
        clickText: String,
        assertTitle: String,
        assertContent: String,
        position: Int
    ) {

        val textViewTitle = onView(
            allOf(
                withText(clickText),
                childAtPosition(
                    allOf(
                        withId(R.id.indexGroup),
                        childAtPosition(
                            withId(R.id.indexScrollGroup),
                            0
                        )
                    ),
                    position
                ),
                isDisplayed()
            )
        )
        textViewTitle.perform(click())


        val textView5 = onView(
            allOf(
                withId(R.id.supportPageTitle), withText(assertTitle),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(withText(assertTitle)))

        val textView6 = onView(
            allOf(
                withId(R.id.supportPageDescription), withText(
                    assertContent
                ),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.indexScrollGroup),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView6.check(
            matches(
                withText(
                    assertContent
                )
            )
        )
        back()
    }

    private fun back() {
        val appCompatImageView = onView(
            allOf(
                withId(R.id.faqImage),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())
    }


}
