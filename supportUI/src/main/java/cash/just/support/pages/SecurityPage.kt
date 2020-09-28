@file:Suppress("UNCHECKED_CAST", "UNUSED")
package cash.just.support.pages

import cash.just.support.BaseSupportPage

enum class SecurityPage(private val title: String, private val description: String) :
    BaseSupportPage {
    WHY_WRITE_DOWN_RECOVERY(
        "Why do I need to write down my recovery key?",
        "The Coinsquare Wallet App connects directly to the cryptocurrency networks, where your wallets are stored. The \"keys\" to access your wallets are securely stored in your phone and nowhere else. The Coinsquare Wallet app will provide you with a unique \"Recovery Key,\" which is a list of 12-words which can be used to recreate the keys to your wallet. If you ever lose or upgrade your phone, this \"Recovery Key\" is required to regain access to your funds.\n\nPlease write down each word, in order, on a piece of paper. We recommend storing your Recovery Key in a safe, safety deposit box at a bank, or wherever you might keep passports, birth certificates, or other important documents. Anyone who has your Recovery Key can access your money, even if they don't have your wallet PIN, so keep it private and don't show it to others!"
    ),
    HOW_TO_RESET(
        "How do I reset my PIN with my recovery key?",
        "Every time you create a new wallet with Coinsquare, you are provided with a \"Recovery Key,\" which is a list of 12 words that can be used to restore access to your wallet if you ever lose or upgrade your phone. You can also use your Recovery Key to reset your wallet PIN if you forget it.\n\nIf you have incorrectly entered your PIN several times, you will be able to reset your PIN using your recovery key. Enter the 12 words from your Recovery Key in the correct order, and you will be able to choose a new PIN."
    ),
    HOW_TO_CONFIRM(
        "How do I confirm my recovery key?",
        "It is very important that your Recovery Key is written down correctly, and in the correct order. Enter the words requested to make sure everything is correct. For example, \"Word #1\" means the first word of your Recovery Key, \"Word #2\" means the second word, etc. "
    ),
    FINGERPRINT(
        "What is fingerprint authentication?",
        "Many Android devices feature a fingerprint reader. You can use your fingerprint to unlock your Coinsquare wallet and authorize transactions instead of entering your passcode. You will still be required to enter your passcode periodically for increased security.\n\nIn order to use this feature in the Coinsquare Wallet app, fingerprint authorization must be enabled on your device. See the phone manufacturer's manual for more information."
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
