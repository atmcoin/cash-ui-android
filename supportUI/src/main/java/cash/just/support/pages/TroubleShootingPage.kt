@file:Suppress("UNCHECKED_CAST", "UNUSED")
package cash.just.support.pages

import cash.just.support.BaseSupportPage

enum class TroubleShootingPage(private val title: String, private val description: String) : BaseSupportPage {
        ERROR_PUBLISH_TRANSACTION_P2P(
                "Error: Could not publish transaction",
                "When the wallet is in P2P mode (the fastsync setting is off), you'll need to wait until the sync is complete before publishing any transaction."
        );

        override fun title(): String {
                return this.title
        }

        override fun description(): String {
                return this.description
        }

        companion object {
                fun pages(): Array<BaseSupportPage> {
                        return values() as Array<BaseSupportPage>
                }
        }
}