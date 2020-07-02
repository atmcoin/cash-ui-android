package cash.just.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetSupportDialogFragment : BottomSheetDialogFragment() {
    companion object {
        private const val ARG_TITLE : String = "ARG_TITLE"
        private const val ARG_DESCRIPTION : String = "ARG_DESCRIPTION"

        fun newInstance(page:SupportPage) : BottomSheetDialogFragment {
            val fragment = BottomSheetSupportDialogFragment()
            val bundle = Bundle()
            bundle.putString(ARG_TITLE, page.title)
            bundle.putString(ARG_DESCRIPTION, page.description)
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

        arguments?.let {
            val title = it.getString(ARG_TITLE)
            val description = it.getString(ARG_DESCRIPTION)
            if (title != null && description != null) {
                textView(view, R.id.supportPageTitle).text = title
                textView(view, R.id.supportPageDescription).text = description
            } else {
                missingArguments()
            }
        } ?:run {
            missingArguments()
        }

        val version = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        textView(view, R.id.versionNumber).text = version

        textView(view, R.id.privacyLink).setOnClickListener {
            launchWebsite("https://coinsquareatm.com/privacy-policy.html")
        }

        return view
    }

    private fun missingArguments(){
        throw IllegalStateException("One or more arguments $ARG_TITLE, $ARG_DESCRIPTION not found, did you call #newInstance()")
    }

    private fun textView(view:View, id:Int): TextView{
        return view.findViewById(id)
    }
}