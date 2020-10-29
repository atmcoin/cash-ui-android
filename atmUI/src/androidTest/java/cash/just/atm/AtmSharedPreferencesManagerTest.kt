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
class AtmSharedPreferencesManagerTest {
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun before() {
        AtmSharedPreferencesManager.clear(appContext)
    }

    @Test
    fun test_profile_fields() {
        // Context of the app under test.
        val phone = "123"
        val name = "lionel"
        val surname = "messi"
        val email = "messi@fcb.com"

        AtmSharedPreferencesManager.setPhone(appContext, phone)
        assertEquals(phone, AtmSharedPreferencesManager.getPhone(appContext))

        AtmSharedPreferencesManager.setFirstName(appContext, name)
        assertEquals(name, AtmSharedPreferencesManager.getFirstName(appContext))

        AtmSharedPreferencesManager.setLastName(appContext, surname)
        assertEquals(surname, AtmSharedPreferencesManager.getLastName(appContext))

        AtmSharedPreferencesManager.setEmail(appContext, email)
        assertEquals(email, AtmSharedPreferencesManager.getEmail(appContext))
    }

    @Test
    fun test_atm_requests() {
        val request1 = "111111"
        val request2 = "222222"
        AtmSharedPreferencesManager.setWithdrawalRequest(appContext, request1)
        val list1 = AtmSharedPreferencesManager.getWithdrawalRequests(appContext)
        assertNotEquals(list1, null)
        assertEquals(list1!!.size, 1)
        assertEquals(list1.take(1)[0], request1)

        AtmSharedPreferencesManager.setWithdrawalRequest(appContext, request2)
        val list2 = AtmSharedPreferencesManager.getWithdrawalRequests(appContext)
        assertNotEquals(list2, null)
        assertEquals(list2!!.size, 2)
        assertEquals(list2.take(2)[0], request1)
        assertEquals(list2.take(2)[1], request2)
    }
}
