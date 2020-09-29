package cash.just.support

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cash.just.support.pages.SupportPagesLoader
import cash.just.support.pages.Topic
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class IndexDialogFragment : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_support_index, container, false)
        val group = view.findViewById<LinearLayout>(R.id.indexGroup)

        createViews(group)

        SupportFooterHelper.populate(view, this)

        return view
    }

    private fun createViews(rootView:LinearLayout) {
        val support = SupportPagesLoader(requireContext())

        support.pages().forEach { page ->
            val view = createTextView(requireContext(), page.title)
            rootView.addView(view, getParams())
            view.setOnClickListener {
                showDetailDialog(Topic.valueOf(page.id))
            }
        }
    }

    private fun showDetailDialog(topic: Topic) {
        activity?.let {
            val fragment = CashSupport.Builder().detail(topic, true).build().createDialogFragment()
            fragment.show(it.supportFragmentManager, "tag")
        }
    }

    private fun getParams() : LinearLayout.LayoutParams {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 50
        return params
    }

    private fun createTextView(context: Context, title:String):TextView {
        val textView = TextView(context)
        textView.text = title
        textView.textSize = 20f
        textView.setTextColor(Color.BLACK)
        return textView
    }
}