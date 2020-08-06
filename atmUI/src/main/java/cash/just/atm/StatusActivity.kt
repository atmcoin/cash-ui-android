package cash.just.atm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class StatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        supportActionBar?.hide()
    }
}
