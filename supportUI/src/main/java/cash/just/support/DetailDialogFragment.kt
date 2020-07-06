package cash.just.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailDialogFragment : BottomSheetDialogFragment() {
    companion object {
        private const val ARG_TITLE : String = "ARG_TITLE"
        private const val ARG_DESCRIPTION : String = "ARG_DESCRIPTION"
        private const val ARG_FROM_INDEX : String = "ARG_FROM_INDEX"

        fun newInstance(page:BaseSupportPage, fromIndex:Boolean = false) : BottomSheetDialogFragment {
            val fragment = DetailDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_TITLE, page.title())
            bundle.putString(ARG_DESCRIPTION, page.description())
            bundle.putBoolean(ARG_FROM_INDEX, fromIndex)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_support_page, container, false)
        val supportTitle = textView(view, R.id.supportPageTitle)
        arguments?.let {
            val title = it.getString(ARG_TITLE)
            val description = it.getString(ARG_DESCRIPTION)
            if (title != null && description != null) {
                supportTitle.text = title
                textView(view, R.id.supportPageDescription).text = description
            } else {
                missingArguments()
            }
        } ?:run {
            missingArguments()
        }

        view.findViewById<ImageView>(R.id.faqImage).setOnClickListener {
            arguments?.let {
                if (!it.getBoolean(ARG_FROM_INDEX)) {
                    openIndexSupport()
                }
            }
            dialog?.dismiss()
        }

        SupportFooterHelper.populate(view, this)

        return view
    }

    private fun missingArguments() {
        throw IllegalStateException("One or more arguments $ARG_TITLE, $ARG_DESCRIPTION not found, did you call #newInstance()")
    }

    private fun openIndexSupport() {
        val fragment = CashSupport.Builder().build().createDialogFragment()
        fragment.show(activity!!.supportFragmentManager, "tag")
    }

    private fun textView(view:View, id:Int): TextView {
        return view.findViewById(id)
    }
}