package cash.just.ui.sample

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cash.just.atm.base.AtmResult
import cash.just.atm.model.BitcoinServer
import cash.just.sdk.Cash
import cash.just.support.context
import cash.just.ui.CashUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_MAP = 0x01
        private const val REQUEST_CODE_LIST = 0x02
        private const val REQUEST_CODE_STATUS = 0x03
    }

    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BitcoinServer.setServer(Cash.BtcNetwork.TEST_NET)
        CashUI.init(Cash.BtcNetwork.TEST_NET)
        serverToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                CashUI.init(Cash.BtcNetwork.TEST_NET)
                BitcoinServer.setServer(Cash.BtcNetwork.TEST_NET)
            } else {
                CashUI.init(Cash.BtcNetwork.MAIN_NET)
                BitcoinServer.setServer(Cash.BtcNetwork.MAIN_NET)
            }
        }

        functions = Firebase.functions

        getToken()

        openMaps.setOnClickListener {
            CashUI.startCashOutActivityForResult(this@MainActivity, REQUEST_CODE_MAP)
        }

        openActivity.setOnClickListener {
            CashUI.showStatusList(this@MainActivity, REQUEST_CODE_LIST)
        }

        openStatus.setOnClickListener {
            CashUI.showStatus(this@MainActivity, cashCode.text.toString(), REQUEST_CODE_STATUS)
        }

        openSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MAP || requestCode == REQUEST_CODE_LIST || requestCode == REQUEST_CODE_STATUS) {
            when (resultCode) {
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(context, "Result Cancelled", Toast.LENGTH_SHORT).show()
                    consoleLog.setText("Result Cancelled")
                }
                Activity.RESULT_OK -> {
                    Toast.makeText(context, "Result Ok", Toast.LENGTH_SHORT).show()
                    data?.let {
                        CashUI.getResult(it)?.let { result ->
                            when (result) {
                                AtmResult.SEND -> {
                                    val send = CashUI.getSendData(it)
                                    consoleLog.setText("Result OK \n" + send.toString())
                                }
                                AtmResult.DETAILS -> {
                                    val details = CashUI.getDetailsData(it)
                                    consoleLog.setText("Result OK \n" + details.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.tag(TAG).w(task.exception, "Fetching FCM registration token failed")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result

            registerToken(token)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val e = task.exception
                        if (e is FirebaseFunctionsException) {
                            val code = e.code
                            val details = e.details
                            Timber.tag(TAG).d("${code}:  $details")
                        }
                    } else {
                        Timber.tag(TAG).d("Task successful")
                    }
                })

            Timber.tag(TAG).d(token)
        })
    }

    private fun registerToken(token: String?): Task<String>  {
        val packageVersion = getPackageVersion()
        val data = hashMapOf(
            "fcmToken" to token,
            "deviceId" to Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            ),
            "phone" to "",
            "deviceModel" to Build.MODEL,
            "appVersion" to packageVersion,
        )
        return functions
            .getHttpsCallable("registerToken")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.tag(TAG).d("task unsuccessful: ${task.exception}")
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                        Timber.tag(TAG).d("${code}: $details")
                    }
                } else {
                    Timber.tag(TAG).d("Task successfull")
                }
            })
    }

    private fun getPackageVersion(): Any {
        return try {
            val pInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}