package cash.just.atm.base

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import cash.just.support.BuildConfig
import cash.just.support.R
import com.google.android.material.snackbar.Snackbar
import com.square.project.base.context
import com.square.project.base.getThemeResource
import timber.log.Timber

fun showError(owner: LifecycleOwner, t: Throwable?) = showSnackBar(owner, if (BuildConfig.DEBUG) t?.toString() else t?.message, 0, null)?.errorColour()
/**
 * Toast Alerts
 */
fun showToast(owner: LifecycleOwner, message: String): Toast? {
    return Toast.makeText(owner.context, message, Toast.LENGTH_LONG).also { toast ->
        toast.show()

        val backgroundColour = owner.context.getThemeResource(android.R.attr.colorPrimaryDark)
        val textColour = owner.context.getThemeResource(android.R.attr.textColorSecondaryInverse)
        toast.adjustColour(backgroundColour, textColour)
    }
}
fun Toast.adjustColour(@ColorRes background: Int, @ColorRes text: Int): Toast {
    val textView: TextView = view.findViewById(android.R.id.message)
    textView.setTextColor(ContextCompat.getColor(view.context, text))
    view.backgroundTintList = ContextCompat.getColorStateList(view.context, background)
    return this
}

/**
 * SnackBars Alerts
 */
fun showSnackBar(owner: LifecycleOwner, message: CharSequence?, @StringRes actionText: Int = 0, action: (() -> Unit)? = null): Snackbar? {
    var alert: Snackbar? = null
    if (message != null) {
        val root = owner.findCoordinatorOrRoot()
        alert = if (action != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE).setAction(actionText) { action.invoke() }
        } else {
            Snackbar.make(root, message, if (BuildConfig.DEBUG) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG)
        }
        if (BuildConfig.DEBUG && actionText == 0) {
            try {
                alert.setAction("Copy") {
                    val clipboard = owner.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Debug Info", message)
                    clipboard.setPrimaryClip(clip)
                }
            } catch (exception: IllegalStateException) {
               //java.lang.IllegalStateException: Fragment LoginFragment{46582ad (7ca5fe04-69ce-4ccf-9577-86d7f8c65df5)} not attached to a context.
                Timber.e(exception)
            }
        }
        alert.adjustMessageView()
        alert.show()
    }
    return alert
}

fun Snackbar.adjustMessageView(): Snackbar {
    val messageView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    messageView.setOnClickListener { dismiss() }
    if (BuildConfig.DEBUG) {
        messageView.maxLines = 10
    }
    return this
}

@SuppressLint("PrivateResource")
fun Snackbar.errorColour(): Snackbar{
    val backgroundColour = context.getThemeResource(android.R.attr.colorError)
    val textColour = context.getThemeResource(android.R.attr.textColorSecondaryInverse)
    return adjustColour(backgroundColour, textColour)
}

fun Snackbar.adjustColour(@DrawableRes background: Int, @ColorRes text: Int): Snackbar {
    view.setBackgroundResource(background)
    val messageView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    val textColour = ContextCompat.getColor(context, text)
    messageView.setTextColor(textColour)
    setActionTextColor(textColour)
    return this
}

private fun LifecycleOwner.findCoordinatorOrRoot(): View {
    val activity = if (this is Fragment) {
        // fragment contains snackbar, display here instead
        view?.findViewById<View>(R.id.snackBarContainer)?.let { return it }
        view?.findViewById<View>(R.id.coordinator)?.let { return it }
        view?.findViewById<View>(R.id.nav_host_fragment)?.let { return it }
        this.requireActivity()
    } else {
        this as FragmentActivity
    }
    return activity.findViewById<View>(R.id.snackBarContainer)
            ?: activity.findViewById(R.id.coordinator)
            ?: activity.findViewById(R.id.nav_host_fragment)
            ?: activity.findViewById(android.R.id.content)

}
