package cash.just.atm

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import cash.just.sdk.Cash
import cash.just.support.CashSupport

interface CashUIProtocol {
  fun init(network: Cash.BtcNetwork)
  fun startCashOutActivityForResult(activity: Activity, requestCode:Int)
  fun showStatusList(context: Context)
  fun showStatus(context: Context, code:String)
  fun getResult(intent:Intent): AtmResult?
  fun getSendData(intent:Intent): SendDataResult?
  fun getDetailsData(intent:Intent): DetailsDataResult?
  fun showSupportPage(builder: CashSupport.Builder, fragmentManager: FragmentManager)
}