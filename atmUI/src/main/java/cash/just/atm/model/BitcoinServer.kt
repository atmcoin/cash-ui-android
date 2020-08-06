package cash.just.atm.model

import cash.just.sdk.Cash

object BitcoinServer {
    private var server = Cash.BtcNetwork.TEST_NET
    fun setServer(btcServer: Cash.BtcNetwork) {
        server = btcServer
    }

    fun getServer(): Cash.BtcNetwork {
        return server
    }
}