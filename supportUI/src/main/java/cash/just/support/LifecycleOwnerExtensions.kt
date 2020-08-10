package cash.just.support

import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

fun LifecycleOwner.launchWebsite(url: String) {
    val customTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onServiceDisconnected(name: ComponentName?) {}
        override fun onCustomTabsServiceConnected(componentName: ComponentName, customTabsClient: CustomTabsClient) {
            customTabsClient.warmup(0L)
        }
    }

    CustomTabsClient.bindCustomTabsService(context, url, customTabsServiceConnection)

    val builder = CustomTabsIntent.Builder()
//    builder.setToolbarColor(backgroundColour)
    builder.setShowTitle(true)

    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}

val LifecycleOwner.context : Context
    get() = (this as? Fragment)?.requireContext() ?: this as Context

fun Context.lifeCycleOwner() : LifecycleOwner {
    lateinit var context: Context
    while (this !is LifecycleOwner) {
        context = (this as ContextWrapper).baseContext;
    }
    return context as LifecycleOwner
}