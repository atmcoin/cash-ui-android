package cash.just.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import cash.just.atm.base.AtmResult
import cash.just.atm.base.DetailsDataResult
import cash.just.atm.base.SendDataResult
import cash.just.sdk.Cash
import cash.just.support.CashSupport

interface CashUIProtocol {
  fun init(network: Cash.BtcNetwork)
  fun startCashOutActivityForResult(activity: Activity, requestCode:Int)
  fun showStatusList(activity: Activity, requestCode:Int)
  fun showStatus(activity: Activity, code:String, requestCode:Int)
  fun getResult(intent:Intent): AtmResult?
  fun getSendData(intent:Intent): SendDataResult?
  fun getDetailsData(intent:Intent): DetailsDataResult?
  fun showSupportPage(builder: CashSupport.Builder, fragmentManager: FragmentManager)
}