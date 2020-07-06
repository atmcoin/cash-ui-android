package cash.just.support

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

object SupportFooterHelper {
    fun populate(view:View, lifecycleOwner: LifecycleOwner) {
        textView(view, R.id.versionNumber).text = BuildConfig.VERSION_NAME

        textView(view, R.id.privacyLink).setOnClickListener {
            Timber.d("Click on privacyLink")
            lifecycleOwner.launchWebsite("https://coinsquareatm.com/privacy-policy.html")
        }
    }

    private fun textView(view: View, id:Int): TextView {
        return view.findViewById(id)
    }
}