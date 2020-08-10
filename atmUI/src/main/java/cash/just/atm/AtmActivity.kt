package cash.just.atm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import cash.just.sdk.model.CashStatus
import kotlinx.android.synthetic.main.activity_atm.*
import java.io.Serializable

interface AtmFlow {
    fun onSend(btcAmount: String, address: String)
    fun onDetails(secureCode: String, cashCodeStatus: CashStatus)
}

enum class AtmResult { SEND, DETAILS }

class SendDataResult(val btcAmount:String, val address:String):Serializable
class DetailsDataResult(val secureCode:String, val cashCodeStatus: CashStatus):Serializable

class AtmActivity : AppCompatActivity(), AtmFlow {
    companion object {
        const val ARGS_RESULT = "ARGS_RESULT"
        const val ARGS_DATA_RESULT = "ARGS_DATA_RESULT"
        fun getResult(intent:Intent):AtmResult? {
            if (intent != null && intent.hasExtra(ARGS_RESULT)) {
                return intent.getSerializableExtra(ARGS_RESULT) as AtmResult
            }
            return null
        }

        fun getSendData(intent:Intent):SendDataResult? {
            if (intent.hasExtra(ARGS_RESULT)) {
                return intent.getSerializableExtra(ARGS_DATA_RESULT) as SendDataResult
            }
            return null
        }

        fun getDetailsData(intent:Intent):DetailsDataResult? {
            if (intent.hasExtra(ARGS_RESULT)) {
                return intent.getSerializableExtra(ARGS_DATA_RESULT) as DetailsDataResult
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
        setResult(RESULT_OK, buildIntent(SendDataResult(btcAmount, address)))
        finish()
    }

    override fun onDetails(secureCode: String, cashCodeStatus: CashStatus) {
        setResult(RESULT_OK, buildIntent(DetailsDataResult(secureCode, cashCodeStatus)))
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
