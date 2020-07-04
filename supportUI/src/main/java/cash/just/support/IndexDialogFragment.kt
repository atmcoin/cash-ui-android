package cash.just.support

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IndexDialogFragment : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_support_index, container, false)
        val group = view.findViewById<LinearLayout>(R.id.indexGroup)
        SupportPage.values().forEach { page ->
            val textView = TextView(context)
            textView.text = page.title
            textView.textSize = 20f
            textView.setTextColor(Color.parseColor("A9A9A9"))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = 50
            group.addView(textView, params)

            textView.setOnClickListener {
                val fragment = CashSupport.Builder().detail(page).build().createDialogFragment()
                fragment.show(activity!!.supportFragmentManager, "tag")
            }
        }
        SupportFooterHelper.populate(view)

        return view
    }
}