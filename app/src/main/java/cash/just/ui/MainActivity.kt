package cash.just.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cash.just.support.CashSupport
import cash.just.support.SupportPage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButtonWithText("INDEX").setOnClickListener {
            val fragment = CashSupport.Builder().build().createDialogFragment()
            fragment.show(supportFragmentManager, "tag")
        }

        SupportPage.values().forEach { page ->
            addButtonWithText(page.title).setOnClickListener {
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
