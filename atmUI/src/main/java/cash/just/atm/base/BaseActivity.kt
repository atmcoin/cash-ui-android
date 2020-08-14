package cash.just.atm.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Calling defaultNightMode will change the theme of the host app
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
    }
}
