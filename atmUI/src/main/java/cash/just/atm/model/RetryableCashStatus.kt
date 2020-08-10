package cash.just.atm.model

import cash.just.sdk.model.CashStatus
import java.io.Serializable

data class RetryableCashStatus(val secureCode:String, val cashStatus:CashStatus) : Serializable