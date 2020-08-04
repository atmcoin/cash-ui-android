package cash.just.atm

import cash.just.sdk.BuildConfig
import cash.just.sdk.Cash

object BitcoinServer {
    fun getServer(): Cash.BtcNetwork{
        return if (BuildConfig.FLAVOR.contains("testnet", true)) {
            Cash.BtcNetwork.TEST_NET
        } else {
            Cash.BtcNetwork.MAIN_NET
        }
    }
}