package cash.just.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cash.just.atm.model.BitcoinServer
import cash.just.sdk.Cash
import cash.just.support.BaseSupportPage
import cash.just.support.CashSupport
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButtonWithText("INDEX").setOnClickListener {
            val fragment = CashSupport.Builder().build().createDialogFragment()
            fragment.show(supportFragmentManager, "tag")
        }


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
        createButtons(BaseSupportPage.allPages())

        openMaps.setOnClickListener {
            CashUI.startCashOutActivityForResult(this@MainActivity, 0)
        }

        openActivity.setOnClickListener {
            CashUI.showStatusList(this@MainActivity)
        }
    }

    private fun createButtons(pages:Array<BaseSupportPage>){
        pages.forEach { page ->
            addButtonWithText(page.title()).setOnClickListener {
                CashUI.showSupportPage(CashSupport.Builder().detail(page), supportFragmentManager)
            }
        }
    }

    private fun addButtonWithText(title:String) : Button {
        val button = Button(this)
        button.text = title
        rootView.addView(button)
        return button
    }
}
