package cash.just.ui.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import cash.just.support.pages.SupportPagesLoader
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class SupportTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(SupportActivity::class.java)

    @Test
    fun testSupportEnterList() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var int = 0
        SupportPagesLoader(context).pages().forEach {
            onView(withContentDescription(int.toString())).perform(scrollTo())
            onView(withText(it.title)).perform(scrollTo()).perform(click())
            onView(withId(R.id.supportPageTitle)).check(matches(withText(it.title)))
            onView(withId(R.id.supportPageDescription)).check(matches(withText(it.content)))
            onView(withId(R.id.faqImage)).perform(click())
            int++
        }
    }
}
