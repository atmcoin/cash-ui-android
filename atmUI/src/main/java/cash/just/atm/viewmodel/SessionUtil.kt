package cash.just.atm.viewmodel

import cash.just.atm.model.BitcoinServer
import cash.just.sdk.Cash
import cash.just.sdk.CashSDK
import java.util.concurrent.CountDownLatch

class SessionUtil{
    companion object {
        fun checkSession() {
            if (!CashSDK.isSessionCreated()) {
                if (!createSession()) {
                    throw IllegalStateException("failed to create session")
                }
            }
        }

        private fun createSession(): Boolean {
            val countDownLatch = CountDownLatch(1)

            CashSDK.createGuestSession(BitcoinServer.getServer(), object : Cash.SessionCallback {
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
}