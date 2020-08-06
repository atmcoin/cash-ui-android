package cash.just.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cash.just.atm.AtmActivity
import cash.just.atm.StatusActivity
import cash.just.atm.model.BitcoinServer
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

        BitcoinServer.setServer(cash.just.sdk.Cash.BtcNetwork.TEST_NET)
        serverToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                BitcoinServer.setServer(cash.just.sdk.Cash.BtcNetwork.TEST_NET)
            } else {
                BitcoinServer.setServer(cash.just.sdk.Cash.BtcNetwork.MAIN_NET)
            }
        }
        createButtons(BaseSupportPage.allPages())

        openMaps.setOnClickListener {
            startActivity(Intent(it.context, AtmActivity::class.java))
        }

        openActivity.setOnClickListener {
            startActivity(Intent(it.context, StatusActivity::class.java))
        }
    }

    private fun createButtons(pages:Array<BaseSupportPage>){
        pages.forEach { page ->
            addButtonWithText(page.title()).setOnClickListener {
                val fragment = CashSupport.Builder().detail(page).build().createDialogFragment()
                fragment.show(supportFragmentManager, "tag")
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
