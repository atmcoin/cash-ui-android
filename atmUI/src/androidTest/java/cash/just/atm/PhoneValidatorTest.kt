package cash.just.atm

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PhoneValidatorTest2 {

    @Test
    fun testSpanishNumber() {
        assert(PhoneValidator("+34655933720").isValid())
        assert(PhoneValidator("0034655933720").isValid())
        assert(PhoneValidator("34655933720").isValid())
    }

    @Test
    fun testAmericanNumber() {
        assert(PhoneValidator("+15555551234").isValid())
        assert(PhoneValidator("5555551234").isValid())
        assert(PhoneValidator("00015555551234").isValid())
        assert(PhoneValidator("(555) 555-1234").isValid())
    }

    @Test
    fun testInvalidNumbers() {
        assertFalse(PhoneValidator("..15555551234").isValid())
        assertFalse(PhoneValidator("5555551234f").isValid())
    }
}
