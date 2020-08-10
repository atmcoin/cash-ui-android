package cash.just.atm

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import cash.just.sdk.Cash
import cash.just.support.CashSupport
import java.lang.IllegalStateException

object CashUI : CashUIProtocol {
  private val cashUI = CashUserInterfaceImpl()
  private var init:Boolean = false

  override fun init(network: Cash.BtcNetwork) {
    cashUI.init(network)
    init = true
  }

  override fun startCashOutActivityForResult(activity: Activity, requestCode:Int) {
    checkInit()
    cashUI.startCashOutActivityForResult(activity, requestCode)
  }

  override fun showStatusList(context: Context) {
    checkInit()
    cashUI.showStatusList(context)
  }

  override fun showStatus(context: Context, code: String) {
    checkInit()
    cashUI.showStatus(context, code)
  }

  override fun getResult(intent:Intent): AtmResult? {
    checkInit()
    return cashUI.getResult(intent)
  }

  override fun getSendData(intent:Intent): SendDataResult? {
    checkInit()
    return cashUI.getSendData(intent)
  }

  override fun getDetailsData(intent:Intent): DetailsDataResult? {
    checkInit()
    return cashUI.getDetailsData(intent)
  }

  override fun showSupportPage(builder: CashSupport.Builder, fragmentManager: FragmentManager) {
    checkInit()
    cashUI.showSupportPage(builder, fragmentManager)
  }

  private fun checkInit(){
    if (!init) {
      throw IllegalStateException(CashUI::class.java.simpleName + " was not initialized, did you call #init()?")
    }
  }
}