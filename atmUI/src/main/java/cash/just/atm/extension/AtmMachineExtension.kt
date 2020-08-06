package cash.just.atm.extension

import cash.just.sdk.model.AtmMachine

fun AtmMachine.getFullAddress() : String {
    return if (addressDesc.contains(city)) {
        addressDesc
    } else {
        "$addressDesc $city"
    }
}