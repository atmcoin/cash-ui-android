package cash.just.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cash.just.support.BottomSheetSupportDialogFragment
import cash.just.support.SupportPage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SupportPage.values().forEach { page ->
            val button = Button(this)
            button.text = page.title
            rootView.addView(button)
            button.setOnClickListener {
                val fragment = BottomSheetSupportDialogFragment.newInstance(page)
                fragment.show(supportFragmentManager, "tag")
            }
        }
    }
}
