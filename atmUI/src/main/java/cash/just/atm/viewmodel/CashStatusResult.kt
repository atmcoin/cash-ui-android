package cash.just.atm.viewmodel

import cash.just.sdk.model.CashStatus

data class CashStatusResult(val cashCode:String, val status: CashStatus)
