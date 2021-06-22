package cash.just.support

import android.content.pm.PackageInfo
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber


object SupportFooterHelper {
    fun populate(view:View, lifecycleOwner: LifecycleOwner) {
        val context = view.context
        val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val version = pInfo.versionName //Version Name
        val verCode = pInfo.versionCode //Version Code

        textView(view, R.id.versionNumber).text = "$version ($verCode))"

        textView(view, R.id.privacyLink).setOnClickListener {
            Timber.d("Click on privacyLink")
            lifecycleOwner.launchWebsite("https://www.just.cash/terms-of-use.html")
        }
    }

    private fun textView(view: View, id:Int): TextView {
        return view.findViewById(id)
    }
}