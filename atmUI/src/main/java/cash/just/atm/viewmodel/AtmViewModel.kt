package cash.just.atm.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cash.just.atm.AtmSharedPreferencesManager
import cash.just.atm.base.RequestState
import cash.just.atm.base.execute
import cash.just.atm.model.VerificationType
import cash.just.sdk.CashSDK
import cash.just.sdk.model.AtmMachine
import cash.just.sdk.model.CashStatus
import cash.just.sdk.model.parseError

class AtmViewModel : ViewModel() {
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    fun getAtms() {
        execute(_state) {
            SessionUtil.checkSession()

            val response = CashSDK.getAtmList().execute()
            return@execute response.body()!!.data.items
        }
    }

    fun createCashCode(atm: AtmMachine, amount: String, secureCode: String) {
        execute(_state) {
            SessionUtil.checkSession()

            val response = CashSDK.createCashCode(atm.atmId, amount, secureCode).execute()
            if (response.code() == 200) {
                return@execute response.body()!!.data.items[0]
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

    fun sendVerificationCode(name:String, surname:String, phone:String?, email:String?) {
        execute(_state) {
            SessionUtil.checkSession()

            val type = if (phone == null) VerificationType.PHONE else VerificationType.EMAIL
            val cashCode = CashSDK.sendVerificationCode(name, surname, phone, email).execute().body()!!.data.items[0].result
            return@execute VerificationSent(type, cashCode)
        }
    }

    data class CashCodeStatusResult(val secureCode:String, val cashCodeStatus: CashStatus)
    fun checkCashCodeStatus(context: Context, secureCode:String) {
        execute(_state) {
            SessionUtil.checkSession()

            AtmSharedPreferencesManager.setWithdrawalRequest(
                context,
                secureCode
            )
            val status = CashSDK.checkCashCodeStatus(secureCode).execute().body()!!.data!!.items[0]
            return@execute CashCodeStatusResult(secureCode, status)
        }
    }
}