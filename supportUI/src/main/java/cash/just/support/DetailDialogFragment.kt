package cash.just.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cash.just.support.pages.SupportPagesLoader
import cash.just.support.pages.Topic
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class DetailDialogFragment : BottomSheetDialogFragment() {
    companion object {
        private const val ARG_TOPIC : String = "ARG_TOPIC"
        private const val ARG_FROM_INDEX : String = "ARG_FROM_INDEX"

        fun newInstance(topic: Topic, fromIndex:Boolean = false) : BottomSheetDialogFragment {
            val fragment = DetailDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_TOPIC, topic)
            bundle.putBoolean(ARG_FROM_INDEX, fromIndex)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_support_page, container, false)
        val supportTitle = textView(view, R.id.supportPageTitle)

        arguments?.let {
            val topic = it.getSerializable(ARG_TOPIC)
            val page = SupportPagesLoader(requireContext()).pages().find { page -> page.id == topic.toString() }
            page?.let {
                supportTitle.text = page.title
                textView(view, R.id.supportPageDescription).text = page.content
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
        throw IllegalStateException("One or more arguments $ARG_TOPIC, $ARG_FROM_INDEX not found, did you call #newInstance()")
    }

    private fun openIndexSupport() {
        val fragment = CashSupport.Builder().build().createDialogFragment()
        fragment.show(requireActivity().supportFragmentManager, "tag")
    }

    private fun textView(view:View, id:Int): TextView {
        return view.findViewById(id)
    }
}