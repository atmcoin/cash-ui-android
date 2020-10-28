package cash.just.ui.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import cash.just.support.pages.SupportPagesLoader
import junit.framework.AssertionFailedError
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class SupportTest {

    @Rule
    @JvmField
    var mActivityScenarioRule= ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun testSupportEnterList() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        SupportPagesLoader(context).pages().forEach {

            Thread.sleep(500)
            val textViewTitle =  onView(withText(it.title))
            textViewTitle.perform(click())

            Thread.sleep(500)
            onView(withId(R.id.supportPageTitle)).check(matches(withText(it.title)))
            onView(withId(R.id.supportPageDescription)).check(matches(withText(it.content)))
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
}
