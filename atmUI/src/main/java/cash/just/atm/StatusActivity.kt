package cash.just.atm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import cash.just.atm.base.AtmFlowActivity
import kotlinx.android.synthetic.main.activity_atm.*

class StatusActivity : AtmFlowActivity() {
    companion object {
        private const val ARGS_CODE = "ARGS_CODE"
        fun newCashStatusIntent(context: Context, cashCode:String): Intent {
            val intent = Intent(context, StatusActivity::class.java)
            intent.putExtra(ARGS_CODE, cashCode)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        getCodeFromIntent()?.let {
            findNavController(R.id.nav_host_fragment)
                .navigate(MobileNavigationDirections.startToStatus(it))
        }
    }

     private fun getCodeFromIntent():String? {
        intent?.let {
            if (it.hasExtra(ARGS_CODE)) {
                return it.getStringExtra(ARGS_CODE)
            }
        }
        return null
    }

    override fun onBackPressed() {
        val navController = nav_host_fragment.findNavController()
        when(navController.currentDestination?.id) {
            R.id.statusFragment -> {
                if (getCodeFromIntent() != null) {
                    finish()
                } else {
                    navController.popBackStack()
                }
            } else -> {
                super.onBackPressed()
            }
        }
    }
}
