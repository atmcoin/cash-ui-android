package cash.just.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import cash.just.atm.*
import cash.just.atm.base.AtmFlowActivity
import cash.just.atm.base.AtmResult
import cash.just.atm.base.DetailsDataResult
import cash.just.atm.base.SendDataResult
import cash.just.sdk.Cash
import cash.just.sdk.CashSDK
import cash.just.support.CashSupport
import timber.log.Timber

class CashUserInterfaceImpl : CashUIProtocol {
  override fun init(network: Cash.BtcNetwork) {
    CashSDK.createSession(network, object:Cash.SessionCallback {
      override fun onSessionCreated(sessionKey: String) {
        Timber.d("Session created")
      }
      override fun onError(errorMessage: String?) {
        Timber.e("Failed to create session %s", errorMessage)
      }
    })
  }

  override fun startCashOutActivityForResult(activity: Activity, requestCode:Int) {
    val intent = Intent(activity.baseContext, AtmActivity::class.java)
    activity.startActivityForResult(intent, requestCode)
  }

  override fun showStatusList(activity: Activity, requestCode:Int) {
    activity.startActivityForResult(Intent(activity, StatusActivity::class.java), requestCode)
  }

  override fun showStatus(activity: Activity, code: String, requestCode:Int) {
    val intent = StatusActivity.newCashStatusIntent(activity.baseContext, code)
    activity.startActivityForResult(intent, requestCode)
  }

  override fun getResult(intent:Intent): AtmResult? {
    return AtmFlowActivity.getResult(intent)
  }

  override fun getSendData(intent:Intent): SendDataResult? {
    return AtmFlowActivity.getSendData(intent)
  }

  override fun getDetailsData(intent:Intent): DetailsDataResult? {
    return AtmFlowActivity.getDetailsData(intent)
  }

  override fun showSupportPage(builder: CashSupport.Builder, fragmentManager: FragmentManager) {
    builder.build().createDialogFragment().show(fragmentManager, "supportPage")
  }
}