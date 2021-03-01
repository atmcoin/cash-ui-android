package cash.just.atm.base

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import timber.log.Timber

fun Firebase.saveFunctions() : FirebaseFunctions? {
    try {
        return functions
    } catch (exception : IllegalStateException) {
        Timber.e("Firebase not initialized")
    }
    return null
}