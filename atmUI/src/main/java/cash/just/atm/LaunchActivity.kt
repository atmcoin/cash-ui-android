package cash.just.atm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cash.just.support.R

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        supportActionBar?.hide()
    }
}
