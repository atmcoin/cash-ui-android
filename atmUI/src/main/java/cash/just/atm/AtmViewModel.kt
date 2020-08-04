package cash.just.atm

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cash.just.atm.base.RequestState
import cash.just.atm.base.execute
import cash.just.sdk.Cash
import cash.just.sdk.CashSDK
import java.lang.IllegalStateException
import java.util.concurrent.CountDownLatch

class AtmViewModel : ViewModel() {
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    fun getAtms() {
        execute(_state) {
            if (!CashSDK.isSessionCreated()) {
                if (!createSession()) {
                    throw IllegalStateException("failed to create session")
                }
            }

            val response = CashSDK.getAtmList().execute()
            return@execute response.body()!!.data.items
        }
    }

    private fun createSession() : Boolean {
        val countDownLatch = CountDownLatch(1)

        CashSDK.createSession(BitcoinServer.getServer(), object: Cash.SessionCallback {
            override fun onSessionCreated(sessionKey: String) {
                countDownLatch.countDown()
            }

            override fun onError(errorMessage: String?) {
                countDownLatch.countDown()
            }
        })

        countDownLatch.await()
        return CashSDK.isSessionCreated()
    }
}