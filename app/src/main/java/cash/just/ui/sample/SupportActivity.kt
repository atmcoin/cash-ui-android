package cash.just.ui.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cash.just.support.CashSupport
import cash.just.support.DetailDialogFragment
import cash.just.support.pages.SupportPagesLoader
import cash.just.support.pages.Topic
import kotlinx.android.synthetic.main.activity_main.*

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        addButtonWithText("INDEX", -1).setOnClickListener {
            val fragment = CashSupport.Builder().build().createDialogFragment()
            fragment.show(supportFragmentManager, "tag")
        }
        createButtons()
    }

    private fun createButtons() {
        var value = 0
        SupportPagesLoader(applicationContext).pages().forEach { page ->
            addButtonWithText(page.title, value).setOnClickListener {
                DetailDialogFragment.newInstance(Topic.valueOf(page.id), true).show(supportFragmentManager, "tag")
            }
            value++
        }
    }

    private fun addButtonWithText(title: String, description:Int): Button {
        val button = Button(this)
        button.text = title
        button.contentDescription = description.toString()
        rootView.addView(button)
        return button
    }
}
