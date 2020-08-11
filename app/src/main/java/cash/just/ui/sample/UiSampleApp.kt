package cash.just.ui.sample

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class UiSampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
    }
}