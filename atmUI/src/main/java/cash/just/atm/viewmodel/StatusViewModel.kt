package cash.just.atm.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cash.just.atm.AtmSharedPreferencesManager
import cash.just.atm.base.RequestState
import cash.just.atm.base.execute
import cash.just.sdk.CashSDK

class StatusViewModel : ViewModel() {
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    fun checkCashCodeStatus(cashCode:String) {
        execute(_state) {
            SessionUtil.checkSession()
            return@execute CashSDK.checkCashCodeStatus(cashCode).execute().body()!!.data!!.items[0]
        }
    }

    fun getCashCodes(context: Context) {
        execute(_state) {
            SessionUtil.checkSession()

            val statusList = ArrayList<CashStatusResult>()
            val requests = AtmSharedPreferencesManager.getWithdrawalRequests(context)
            requests?.let {

                it.forEach { code ->
                    val cashCode = CashSDK.checkCashCodeStatus(code).execute().body()!!.data!!.items[0]
                    statusList.add(
                        CashStatusResult(code, cashCode)
                    )
                }
            }

            return@execute statusList
        }
    }
}