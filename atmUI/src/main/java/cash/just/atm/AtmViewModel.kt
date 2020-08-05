package cash.just.atm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cash.just.atm.base.RequestState
import cash.just.atm.base.execute
import cash.just.sdk.Cash
import cash.just.sdk.CashSDK
import cash.just.sdk.model.AtmMachine
import cash.just.sdk.model.parseError
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

    private fun createSession(): Boolean {
        val countDownLatch = CountDownLatch(1)

        CashSDK.createSession(BitcoinServer.getServer(), object : Cash.SessionCallback {
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

    fun createCashCode(atm: AtmMachine, amount: String, code: String) {
        execute(_state) {
            val response = CashSDK.createCashCode(atm.atmId, amount, code).execute()
            if (response.code() == 200) {
                val secureCode = response.body()!!.data.items[0].secureCode
            } else {
                val errorBody = response.errorBody()
                errorBody?.let {
                    throw IllegalStateException(it.parseError().error.server_message)
                } ?: run {
                    throw IllegalStateException(response.code().toString())
                }
            }
        }
    }

    fun checkCashCodeStatus(context: Context, secureCode:String) {
        execute(_state) {
            AtmSharedPreferencesManager.setWithdrawalRequest(context, secureCode)
            return@execute CashSDK.checkCashCodeStatus(secureCode).execute().body()!!.data!!.items[0]
        }
    }
}