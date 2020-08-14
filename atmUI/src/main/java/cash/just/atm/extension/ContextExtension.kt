package cash.just.atm.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun Context.hideKeyboard(editText: EditText?) {
    editText?.let {
        val imm: InputMethodManager? = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}