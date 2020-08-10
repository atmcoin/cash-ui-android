package cash.just.atm

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import cash.just.sdk.Cash
import cash.just.sdk.model.CashStatus

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("cash.just.atm", appContext.packageName)
    }

    @Test
    fun assert() {
        assertTrue(false)
    }

    @Test
    fun test_intent_builder() {
        CashUI.init(Cash.BtcNetwork.TEST_NET)
        val detailsIntent = AtmActivity.buildIntent(DetailsDataResult("securecode", mockCashStatus()))
        assertEquals(CashUI.getResult(detailsIntent), AtmResult.DETAILS)

        val sendIntent = AtmActivity.buildIntent(SendDataResult("23BTC", "234234x32423"))
        assertEquals(CashUI.getResult(sendIntent), AtmResult.SEND)
    }

    private fun mockCashStatus() : CashStatus {
        return CashStatus("code", "status", "address", "3$", "0.5", "3.4", "","23","description", "latitude", "longitude")
    }
}
