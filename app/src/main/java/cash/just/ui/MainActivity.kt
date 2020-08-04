package cash.just.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cash.just.atm.LaunchActivity
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

        createButtons(BaseSupportPage.allPages())

        openMaps.setOnClickListener {
            startActivity(Intent(it.context, LaunchActivity::class.java))
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
