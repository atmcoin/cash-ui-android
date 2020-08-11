package cash.just.atm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.navigation.fragment.findNavController
import cash.just.atm.base.BaseActivity
import cash.just.sdk.model.CashStatus
import kotlinx.android.synthetic.main.activity_atm.*
import timber.log.Timber
import java.io.Serializable

interface AtmFlow {
    fun onSend(btcAmount: String, address: String)
    fun onDetails(secureCode: String, cashCodeStatus: CashStatus)
}

enum class AtmResult { SEND, DETAILS }

data class SendDataResult(val btcAmount:String, val address:String):Serializable
data class DetailsDataResult(val secureCode:String, val cashCodeStatus: CashStatus):Serializable

class AtmActivity : BaseActivity(), AtmFlow {
    companion object {
        private const val ARGS_RESULT = "ARGS_RESULT"
        private const val ARGS_DATA_RESULT = "ARGS_DATA_RESULT"
        fun getResult(resultIntent:Intent):AtmResult? {
            if (resultIntent.hasExtra(ARGS_RESULT)) {
                return resultIntent.getSerializableExtra(ARGS_RESULT) as AtmResult
            }
            return null
        }

        fun getSendData(dataIntent:Intent):SendDataResult? {
            if (dataIntent.hasExtra(ARGS_DATA_RESULT)) {
                return dataIntent.getSerializableExtra(ARGS_DATA_RESULT) as SendDataResult
            }
            return null
        }

        fun getDetailsData(detailsIntent:Intent):DetailsDataResult? {
            if (detailsIntent.hasExtra(ARGS_DATA_RESULT)) {
                return detailsIntent.getSerializableExtra(ARGS_DATA_RESULT) as DetailsDataResult
            }
            return null
        }

        fun buildIntent(result:DetailsDataResult) : Intent {
            val intent = Intent()
            intent.putExtra(ARGS_RESULT, AtmResult.DETAILS)
            intent.putExtra(ARGS_DATA_RESULT, result)
            return intent
        }

        fun buildIntent(result:SendDataResult) : Intent {
            val intent = Intent()
            intent.putExtra(ARGS_RESULT, AtmResult.SEND)
            intent.putExtra(ARGS_DATA_RESULT, result)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atm)
        supportActionBar?.hide()
    }

    override fun onSend(btcAmount: String, address: String) {
        val intent2 = buildIntent(SendDataResult(btcAmount, address))
        Timber.d("david onSend $intent2")
        setResult(RESULT_OK, intent2)
        finish()
    }

    override fun onDetails(secureCode: String, cashCodeStatus: CashStatus) {
        val intent2 = buildIntent(DetailsDataResult(secureCode, cashCodeStatus))
        Timber.d("david onSend $intent2")
        setResult(RESULT_OK, intent2)
        finish()
    }

    override fun onBackPressed() {
        val navController = nav_host_fragment.findNavController()
        when(navController.currentDestination?.id) {
            R.id.atmRequestFragment -> {
                navController.popBackStack()
            } else -> {
                super.onBackPressed()
            }
        }
    }
}
