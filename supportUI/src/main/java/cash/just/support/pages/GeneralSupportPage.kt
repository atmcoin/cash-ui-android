@file:Suppress("UNCHECKED_CAST", "UNUSED")
package cash.just.support.pages

import cash.just.support.BaseSupportPage

enum class GeneralSupportPage(private val title: String, private val description: String) :
    BaseSupportPage {
    GET_STARTED("How do I get started?",
            "Welcome to the Coinsquare Wallet!\n\nThe Coinsquare Wallet app connects directly to the bitcoin network. There are no accounts or usernames with the Coinsquare Wallet app. Instead, you will have a \"wallet\" where you will store your money. It's just like the physical wallet you put your cash in, except that it exists only on the internet and holds digital money.\n" +
            "\n" +
            "If this is your first time using the Coinsquare Wallet App, simply choose \"Get Started\" to start.\n" +
            "\n" +
            "If you have used the Coinsquare Wallet app before and would like to use the same wallet, choose \"Restore Wallet\" and prepare to enter the Recovery Key for the wallet you would like to restore."),
    SEND("How do I send bitcoin?","To send money to someone you need to enter their bitcoin address. There are three different ways to enter this information depending on how you received it:\n" +
            "\n" +
            "\t\t - If you are presented with a QR code, press the \"scan\" button and scan the QR code.\n" +
            "\t\t - If you receive an address through email or text, copy the address to your device's clipboard and press the \"paste\" button.\n" +
            "\t\t - If you receive the address another way, tap the \"To\" label and use the keyboard to enter the address.\n" +
            "\t\t - Once you have entered the payment address, tap the \"Amount\" label and enter the amount you wish to send. The button to the right will allow you to toggle the view between the amount of bitcoin to be sent or another currency.\n" +
            "\n" +
            "\t\t - If you would like to save a note about this transaction, tap the \"Memo\" label. This note will only be visible to you, and will not be sent together with the transaction.\n" +
            "\n" +
            "Important: If bitcoin is sent to the wrong address, it can not be refunded. When sending bitcoin, always check to make sure the address you are sending to is the same as the one that was given to you (comparing the first 5 or 6 digits is usually sufficient to make sure you are using the correct address)."),
    RECEIVE("How do I receive bitcoin?","If you would like to receive bitcoin from someone, you will need to give them your bitcoin address. There are a few ways to do this:\n" +
            "\n" +
            "\t\t - Show them the \"Receive Money\" screen in your Coinsquare Wallet App, and let them scan your QR code.\n" +
            "\t\t - Press the \"Share\" button under the QR code to send them your address via email or text message.\n" +
            "\t\t - Tap the QR code to copy your address to your device's memory. You can now paste your address into a website or other app.\n" +
            "\t\t - The bitcoin address shown on your receive address will change every time you receive money, but old addresses will continue to work.\n" +
            "\n" +
            "Important: If bitcoin is sent to the wrong address, it can not be refunded. When sharing your address, always check to make sure the address you give out is the same as the one shown under your QR code (comparing the first 5 or 6 digits is usually sufficient to make sure you are using the correct address)."),
    IMPORT_WALLET("How do I import a bitcoin paper wallet?","If you receive a QR code labeled as a \"paper wallet\" or \"private key\", you can import it into your Coinsquare app's Bitcoin wallet by following these steps:\n\n" +
            "\t\t 1. On the main screen, tap on Menu.\n" +
            "\t\t 2. Under Preferences, tap on Bitcoin Settings.\n" +
            "\t\t 3. Select Redeem Private Key.\n" +
            "\t\t 4. Tap on Scan Private Key and scan the QR code on your paper wallet.\n" +
            "\t\t 5. Any funds stored on the paper wallet/private key will be sent into your Coinsquare app's Bitcoin wallet, and will not remain on the paper wallet/private key.\n" +
            "\n\n" +
            "NOTE:  If you are importing a bitcoin paper wallet created before the August 1, 2017 fork, and would like to get the bitcoin cash credit, follow the steps here.\n" +
            "\n\n" +
            "If you want to import a wallet created with the Coinsquare Wallet App, use the Unlink from this device function instead."),
    PIN("Why do I need a PIN?","\t\tThe Coinsquare Wallet app requires you to set a PIN to secure your wallet, separate from your device passcode.\n\t\tYou will be required to enter this PIN to view your balance or send money, which keeps your wallet private even if you let someone use your phone or if your phone is stolen by someone who knows your device passcode.\n\t\tDo not forget your wallet PIN! It can only be reset by using your Recovery Key. If you forget your PIN and lose your Recovery Key, your wallet will be lost."),
    RECOVERY_KEY("What is a recovery key?","A recovery key consists of 12 randomly generated words. The app creates the recovery key for you automatically when you start a new wallet. The recovery key is critically important and should be written down and stored in a safe location. In the event of phone theft, destruction, or loss, the recovery key can be used to load your wallet onto a new phone. The key is also required when upgrading your current phone to a new one."),
    WALLET_DISABLED("Why is my wallet disabled?","If you enter an incorrect wallet PIN too many times, your wallet will become disabled for a certain amount of time. This is to prevent someone else from trying to guess your PIN by quickly making many guesses. If your wallet is disabled, wait until the time shown and you will be able to enter your PIN again.\n" +
            "\n" +
            "If you continue to enter the incorrect PIN, the amount of time you will have to wait in between attempts will increase. Eventually, the app will reset and you can start a new wallet.\n" +
            "\n" +
            "If you have the recovery key for your wallet, you can use it to reset your PIN by choosing the appropriate option on the lock screen. ");

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