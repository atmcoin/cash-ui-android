package cash.just.ui

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import cash.just.atm.*
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
        assertEquals("cash.just.ui.test", appContext.packageName)
    }
/**
    @Test
    fun test_intent_builder() {
        CashUI.init(Cash.BtcNetwork.TEST_NET)
        val detailsData = DetailsDataResult("securecode", mockCashStatus())
        val detailsIntent = AtmActivity.buildIntent(detailsData)
        assertEquals(CashUI.getResult(detailsIntent), AtmResult.DETAILS)
        assertEquals(CashUI.getDetailsData(detailsIntent), detailsData)

        val sendData = SendDataResult("23BTC", "234234x32423")
        val sendIntent = AtmActivity.buildIntent(sendData)
        assertEquals(CashUI.getResult(sendIntent), AtmResult.SEND)

        assertEquals(CashUI.getSendData(sendIntent), sendData)
    }

    private fun mockCashStatus() : CashStatus {
        return CashStatus("code", "status", "address", "3$", "0.5", "3.4", "","23","description", "latitude", "longitude")
    }/wo    /
    */
}
