package cash.just.atm.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import cash.just.sdk.model.CashStatus
import timber.log.Timber
import java.io.Serializable

interface AtmFlow {
    fun onSend(btcAmount: String, address: String)
    fun onDetails(secureCode: String, cashCodeStatus: CashStatus)
}

enum class AtmResult { SEND, DETAILS }

data class SendDataResult(val btcAmount:String, val address:String): Serializable
data class DetailsDataResult(val secureCode:String, val cashCodeStatus: CashStatus): Serializable

abstract class AtmFlowActivity : AppCompatActivity(), AtmFlow {
    companion object {
        private const val ARGS_RESULT = "ARGS_RESULT"
        private const val ARGS_DATA_RESULT = "ARGS_DATA_RESULT"
        fun getResult(resultIntent: Intent): AtmResult? {
            if (resultIntent.hasExtra(ARGS_RESULT)) {
                return resultIntent.getSerializableExtra(ARGS_RESULT) as AtmResult
            }
            return null
        }

        fun getSendData(dataIntent: Intent): SendDataResult? {
            if (dataIntent.hasExtra(ARGS_DATA_RESULT)) {
                return dataIntent.getSerializableExtra(ARGS_DATA_RESULT) as SendDataResult
            }
            return null
        }

        fun getDetailsData(detailsIntent: Intent): DetailsDataResult? {
            if (detailsIntent.hasExtra(ARGS_DATA_RESULT)) {
                return detailsIntent.getSerializableExtra(ARGS_DATA_RESULT) as DetailsDataResult
            }
            return null
        }

        fun buildIntent(result: DetailsDataResult) : Intent {
            val intent = Intent()
            intent.putExtra(ARGS_RESULT, AtmResult.DETAILS)
            intent.putExtra(ARGS_DATA_RESULT, result)
            return intent
        }

        fun buildIntent(result: SendDataResult) : Intent {
            val intent = Intent()
            intent.putExtra(ARGS_RESULT, AtmResult.SEND)
            intent.putExtra(ARGS_DATA_RESULT, result)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Calling defaultNightMode will change the theme of the host app
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
    }

    override fun onSend(btcAmount: String, address: String) {
        val intent = buildIntent(SendDataResult(btcAmount, address))
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDetails(secureCode: String, cashCodeStatus: CashStatus) {
        val intent = buildIntent(DetailsDataResult(secureCode, cashCodeStatus))
        setResult(RESULT_OK, intent)
        finish()
    }
}
