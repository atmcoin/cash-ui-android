package cash.just.atm

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import cash.just.atm.base.AtmFlowActivity
import kotlinx.android.synthetic.main.activity_atm.*

class AtmActivity : AtmFlowActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atm)
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
