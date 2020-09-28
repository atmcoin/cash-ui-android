@file:Suppress("UNCHECKED_CAST", "UNUSED")
package cash.just.support.pages

import cash.just.support.BaseSupportPage

enum class SettingPage(private val title: String, private val description: String) :
        BaseSupportPage {
        HOW_WIPE_WALLET(
                "How do I wipe my wallet?",
                "The Coinsquare Wallet App can only have one wallet active at a time. If you choose to start a new wallet or recover an existing wallet, the currently active wallet must be erased first.\n\nIMPORTANT: If you erase your wallet without writing down its Recovery Key, your money will be lost. To prevent this, you must first enter your Recovery Key for the currently active wallet to make sure you have it written down correctly.\n\nFollow these steps to wipe a wallet:\n\n\t\t\t 1 - Tap on Menu in the main screen.\n\t\t\t 2 - Select Security Settings.\n\t\t\t 3 - Tap on Unlink from this device and then on Next.\n\t\t\t 4 - Enter the current wallet’s Recovery Key.\n\t\t\t 5 - Tap on Wipe.\n\nThe wallet will then be erased, and you can create a new wallet or recover a previous wallet. "
        ),
        HOW_RECOVER_WALLET(
                "How do I recover a previous wallet I had?",
                "When you launch the Coinsquare Wallet app for the first time, you will be given the option to create a new wallet or to recover a wallet. If you have a 12 word recovery key from a previous Coinsquare wallet that you would like to recover, follow these steps to recover your wallet:\n\n\t\t\t 1 - Select Restore Wallet.\n\t\t\t 2 - Click Next and enter the 12 word Recovery Key in the correct order for the wallet you want to recover.\n\t\t\t 3 - Set a PIN.\n\t\t\t 4 - Confirm the PIN.\n\t\t\t 5 - Leave the app open and on the screen to allow the wallets to sync to the blockchain.\n\nInitial syncing can take up to 45 minutes or more so please prepare to leave the app open with the screen on for an extended period of time.\nIf you wish to recover a wallet using a recovery key but you already have an active wallet loaded in the Coinsquare Wallet app, you will need to follow the directions to wipe the wallet before beginning the process above. If the wallet you are wiping has funds, be sure to write down the 12 word recovery key for the existing wallet (which can be found in the Security Center) before loading the recovery key for the wallet you wish to recover. Visit this page for help on wiping a wallet. "
        ),
        HOW_CHANGE_CURRENCY(
                "How do I change the local currency displayed in my wallet?",
                "Your Coinsquare wallet will show your cryptocurrency and its equivalent fiat value in your local currency by default. \n\n You can change this to show the value in a different local currency by following these steps:\n\n\t\t\t 1 - On the main screen, tap Menu.\n\t\t\t 2 - Select Preferences and then Display Currency.\n\t\t\t 3 - Choose the currency you would like to use from the list.\n"
        ),
        HOW_BALANCE_WORKS(
                "How does the Coinsquare Bitcoin wallet show my balance in my local currency?",
                "Money kept in your Coinsquare Bitcoin wallet is always stored as bitcoin. The app uses an exchange rate to show you an estimate of how much your bitcoin is worth in your local currency. When you send a transaction and enter the amount to send in your local currency, the app uses the exchange rate to calculate how much bitcoin to send.\n\nIt's possible people using other wallets are using different exchange rates, so the balance sent/received might be slight different than what other person sees.\n\nSince the exchange rate is always changing, the amount of money in your wallet as quoted in your local currency may change over time. However, your bitcoin balance will not change unless you send or receive bitcoin. "
        ),
        HOW_SELECT_NODE(
                "How do I connect to a specific bitcoin node?",
                "The BRD app connects to a random node on the bitcoin network when syncing your wallet. You can choose to connect to a specific node of your preference by following the steps below:\n\t\t\t 2 - Select Preferences and then Bitcoin Settings.\n\t\t\t 3 - Choose Bitcoin Nodes\n\t\t\t 4 - Tap Switch to Manual Mode\n\t\t\t 5 - Enter the Node IP address in the window that pops up.  You may also enter a port, although this is optional.  Tap OK when done.\n\nYour wallet will now sync to the blockchain via your preferred node."
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