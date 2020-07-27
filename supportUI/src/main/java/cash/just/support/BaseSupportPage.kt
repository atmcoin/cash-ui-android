package cash.just.support

import cash.just.support.pages.*

interface BaseSupportPage {
    fun title():String
    fun description():String

    companion object {
        fun allPages(): Array<BaseSupportPage> {
            val list = GeneralSupportPage.pages().toMutableList()
            list.addAll(AtmPage.pages())
            list.addAll(SecurityPage.pages())
            list.addAll(SettingPage.pages())
            list.addAll(TroubleShootingPage.pages())
            return list.toTypedArray()
        }
    }
}