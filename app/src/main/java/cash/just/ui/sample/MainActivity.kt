package cash.just.ui.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cash.just.atm.*
import cash.just.atm.model.BitcoinServer
import cash.just.sdk.Cash
import cash.just.support.BaseSupportPage
import cash.just.support.CashSupport
import cash.just.support.context
import cash.just.ui.CashUI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE = 0x01
    }

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
            CashUI.startCashOutActivityForResult(
                this@MainActivity,
                REQUEST_CODE
            )
        }

        openActivity.setOnClickListener {
            CashUI.showStatusList(this@MainActivity)
        }

        openStatus.setOnClickListener {
            CashUI.showStatus(
                this@MainActivity,
                cashCode.text.toString()
            )
        }
    }

    private fun createButtons(pages:Array<BaseSupportPage>){
        pages.forEach { page ->
            addButtonWithText(page.title()).setOnClickListener {
                CashUI.showSupportPage(
                    CashSupport.Builder().detail(page),
                    supportFragmentManager
                )
            }
        }
    }

    private fun addButtonWithText(title:String) : Button {
        val button = Button(this)
        button.text = title
        rootView.addView(button)
        return button
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            when(resultCode) {
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(context, "Result Cancelled", Toast.LENGTH_SHORT).show()
                    consoleLog.setText("Result Cancelled")
                }
                Activity.RESULT_OK -> {
                    Toast.makeText(context, "Result Ok", Toast.LENGTH_SHORT).show()
                    CashUI.getResult(intent)?.let {
                        when(it) {
                            AtmResult.SEND -> {
                                val send =
                                    CashUI.getSendData(intent)
                                consoleLog.setText("Result OK \n" + send.toString())
                            }
                            AtmResult.DETAILS -> {
                                val details =
                                    CashUI.getSendData(intent)
                                consoleLog.setText("Result OK \n" + details.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
