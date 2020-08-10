package cash.just.atm.viewmodel

import cash.just.atm.model.VerificationType

data class VerificationSent(val type: VerificationType, val cashCode: String)
