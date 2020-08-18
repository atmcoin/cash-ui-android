package cash.just.atm.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ContentLoadingProgressBar
import cash.just.atm.R
import cash.just.atm.base.hideKeyboard

class LoadingButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val KEY_PROGRESS_ENABLED = "KEY_PROGRESS_ENABLED"
        private const val KEY_TEXT = "KEY_TEXT"
        private const val SAVE_SUPER_STATE = "SAVE_SUPER_STATE"
    }

    private val progressView: ContentLoadingProgressBar
    private val button: Button

    var text: CharSequence
        get() = button.text
        set(value) {
            button.text = value
        }

    var isProgressDisplayed: Boolean
        get() = progressView.isShown
        set(value) {
            if (value) showProgress() else hideProgress()
        }

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, 0)
        val buttonStyle = a.getInt(R.styleable.LoadingButton_loadingButtonStyle, 0)
        val buttonText = a.getString(R.styleable.LoadingButton_loadingButtonText)

        val buttonLayout = when (buttonStyle) {
            0 -> R.layout.base_view_button_primary
            1 -> R.layout.base_view_button_primary_highlighted
            2 -> R.layout.base_view_button_secondary
            3 -> R.layout.base_view_button_secondary_dialog
            else -> R.layout.base_view_button_secondary
        }

        val startOn = a.getInt(R.styleable.LoadingButton_loadingButtonState, 0) == 1
        a.recycle()

        View.inflate(context, buttonLayout, this)

        button = findViewById(R.id.loadingButton)
        button.text = buttonText

        progressView = findViewById(R.id.loadingProgress)
        progressView.visibility = View.GONE

        if (startOn) showProgress() else hideProgress()
    }

    fun hideProgress() {
        progressView.hide()
        button.isEnabled = true
    }

    fun showProgress() {
        progressView.show()
        button.isEnabled = false
    }

    fun setLoadingText(text : String){
        button.text = text
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        if (listener != null) {
            button.setOnClickListener {
                (context as? AppCompatActivity)?.currentFocus?.clearFocus()
                // lowers the glitching when put into a post
                post { hideKeyboard() }
                listener.onClick(this)
            }
        } else {
            button.setOnClickListener(null)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putCharSequence(KEY_TEXT, button.text)
            putBoolean(KEY_PROGRESS_ENABLED, isEnabled)
            putParcelable(SAVE_SUPER_STATE, super.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState = state
        if (state is Bundle) {
            superState = state.getParcelable(SAVE_SUPER_STATE)
            button.text = state.getCharSequence(KEY_TEXT)
            // resume loading
            if (!state.getBoolean(KEY_PROGRESS_ENABLED)) {
                showProgress()
            }
        }
        super.onRestoreInstanceState(superState)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        button.isEnabled = enabled
    }
}
