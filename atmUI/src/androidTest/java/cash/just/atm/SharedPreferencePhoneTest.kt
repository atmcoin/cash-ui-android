package cash.just.atm

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SharedPreferencePhoneTest {
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun before() {
        AtmSharedPreferencesManager.clear(appContext)
    }

    @Test
    fun test_shared_preference_phone() {
        // Context of the app under test.
        val phone = "+14371231234"
        AtmSharedPreferencesManager.setPhone(appContext, phone)
        assertEquals(phone, AtmSharedPreferencesManager.getPhone(appContext))
        assertEquals(PhoneValidator(AtmSharedPreferencesManager.getPhone(appContext)).phoneNumberWithoutCountryCode(), "4371231234")
    }

    @Test
    fun test_phone_validator_country_code() {
        //Mexico country code test
        assertEquals(PhoneValidator("+531234567123").phoneNumberWithoutCountryCode(), "1234567123")
        //US/CA country code test
        assertEquals(PhoneValidator("+11234567123").phoneNumberWithoutCountryCode(), "1234567123")
        //No country code test
        assertEquals(PhoneValidator("1234567123").phoneNumberWithoutCountryCode(), "1234567123")
    }
}