@file:Suppress("UNCHECKED_CAST", "UNUSED")
package cash.just.support.pages

import cash.just.support.BaseSupportPage

enum class AtmPage(private val title: String, private val description: String) : BaseSupportPage {
        HOW_TO_BUY("How do I buy Bitcoin from your ATMs?","Purchasing Bitcoin is just as easy and familiar as using an ATM!\n\nAt the ATM, insert your debit card and enter your PIN. Then, select 'Purchase Bitcoin' from the menu.\n\nEnter the amount of Bitcoin you wish to purchase in US Dollars. You will then receive a quoted Bitcoin price.\nOnce you press 'Continue,' your purchase will complete and you will receive a receipt for your transaction, as well as a printout of your paper wallet with your purchased Bitcoin.\n\n*Please ensure that you keep your paper wallet secure, as this is where your Bitcoin is stored. Lost paper wallets cannot be replaced.*\n"),
        DAILY_LIMIT("What is the daily maximum purchase limit?","The daily maximum purchase amount is \$1,000 USD."),
        FEE_CHARGED("Are fees charged for Bitcoin purchases?","A small price mark-up is charged, which can vary with location. Our prices are always quoted in real time and are better than other bitcoin ATMs."),
        CARDS_ACCEPTED("Do you accept credit cards?", "No. At this time, we only accept EMV (chip enabled) debit cards."),
        RECEIPT("Do you provide a transaction receipt?","Yes, you will be provided with a transaction receipt once your purchase is complete, along with a paper wallet which includes the public and private keys that store the value of your Bitcoin purchase.\n\n*Please ensure that you keep your paper wallet secure, as this is where your Bitcoin is stored. Lost paper wallets cannot be replaced.*"),
        ID_VERIFICATION("Is ID verification required at the ATM?","There are no additional verification requirements at the ATM beyond requiring the user's debit card and PIN number."),
        DO_YOU_NEED_WALLET("Do I need to have a crypto wallet to use your ATMs?", "You are NOT required to have a crypto wallet in order to purchase Bitcoin through an ATM.\nWhen you complete your Bitcoin purchase, you will receive a 'paper wallet' receipt which includes your public and private keys. Your Bitcoin is stored on the paper wallet until you decide to transfer the funds to an online Bitcoin wallet"),
        TRANSACTION_FAILED("My transaction failed. What do I do?","If you receive an error message stating your transaction has failed, retrieve your debit card from the ATM and locate another one of our ATMs using the map."),
        LOST_PAPER("I lost my paper wallet! How can I retrieve my funds?","If you lost your paper wallet, please reach out to our Support Team as soon as possible.\n\nUnfortunately, we are not able to guarantee that any lost funds can be retrieved, because the Bitcoin is stored on your paper wallet via the public and private keys."),
        PAPER_WALLET("What is a paper wallet?","A paper wallet is an offline mechanism for storing cryptocurrency. It contains a public and private key, printed onto paper.\n\nThe public key is essentially a wallet address. This can be shared publicly. A private key should only be known to the owner and should never be publicly shared. You may choose to import your paper wallet to a cold storage device, or to an online crypto wallet.");

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