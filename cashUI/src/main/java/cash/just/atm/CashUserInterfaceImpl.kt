package cash.just.atm

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
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
    activity.startActivityForResult(Intent(activity, AtmActivity::class.java), requestCode)
  }

  override fun showStatusList(context: Context) {
    context.startActivity(Intent(context, StatusActivity::class.java))
  }

  override fun showStatus(context: Context, code: String) {
    val intent = StatusActivity.newCashStatusIntent(context,code)
    context.startActivity(intent)
  }

  override fun getResult(intent:Intent): AtmResult? {
    return AtmActivity.getResult(intent)
  }

  override fun getSendData(intent:Intent): SendDataResult? {
    return AtmActivity.getSendData(intent)
  }


  override fun getDetailsData(intent:Intent): DetailsDataResult? {
    return AtmActivity.getDetailsData(intent)
  }

  override fun showSupportPage(builder: CashSupport.Builder, fragmentManager: FragmentManager) {
    builder.build().createDialogFragment().show(fragmentManager, "supportPage")
  }
}