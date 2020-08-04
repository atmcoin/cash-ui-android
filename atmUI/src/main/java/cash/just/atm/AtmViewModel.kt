package cash.just.atm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cash.just.atm.base.RequestState
import cash.just.atm.base.execute
import cash.just.sdk.CashSDK

class AtmViewModel : ViewModel() {
    private val _state = MutableLiveData<RequestState>()
    val state: LiveData<RequestState>
        get() = _state

    fun getAtms() {
        execute(_state) {
            val response = CashSDK.getAtmList().execute()
            return@execute response.body()!!.data.items
        }
    }
}