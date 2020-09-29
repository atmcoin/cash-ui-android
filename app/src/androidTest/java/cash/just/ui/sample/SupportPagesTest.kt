package cash.just.ui.sample

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import cash.just.support.pages.SupportPagesLoader
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SupportPagesTest {
    //TODO: move to androidx.ActivityScenario
    @Rule @JvmField
    val mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("cash.just.ui.sample", appContext.packageName)
    }

    @Test
    fun testSupportPages() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        SupportPagesLoader(appContext).pages().forEach {

            onView(withText(it.title)).perform(ViewActions.scrollTo()).perform(click())

            onView(withId(R.id.supportPageTitle)).check(matches(withText(it.title)))

            onView(withId(R.id.faqImage))
                .perform(click())
        }
    }
}
