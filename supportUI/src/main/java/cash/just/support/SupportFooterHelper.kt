package cash.just.support

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

object SupportFooterHelper {
    fun populate(view:View, lifecycleOwner: LifecycleOwner) {
        val version = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        textView(view, R.id.versionNumber).text = version

        textView(view, R.id.privacyLink).setOnClickListener {
            Timber.d("Click on privayLink")
            lifecycleOwner.launchWebsite("https://coinsquareatm.com/privacy-policy.html")
        }
    }

    private fun textView(view: View, id:Int): TextView {
        return view.findViewById(id)
    }
}