package cash.just.atm

import android.content.Context
import android.content.SharedPreferences

class AtmSharedPreferencesManager {
    companion object {
        private const val APP_SETTINGS = "APP_ATM_PREFERENCES"
        private const val WITHDRAWAL_REQUESTS = "WITHDRAWAL_REQUESTS"
        private const val PHONE = "PHONE"
        private const val FIRST_NAME = "FIRST_NAME"
        private const val LAST_NAME = "LAST_NAME"
        private const val EMAIL = "EMAIL"

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
        }

        fun getPhone(context: Context):String? {
            return getSharedPreferences(context).getString(PHONE, null)
        }

        fun setPhone(context: Context, phone: String) {
            getSharedPreferences(context).edit().putString(PHONE, phone).apply()
        }

        fun getEmail(context: Context):String? {
            return getSharedPreferences(context).getString(EMAIL, null)
        }

        fun setEmail(context: Context, email: String) {
            getSharedPreferences(context).edit().putString(EMAIL, email).apply()
        }

        fun getFirstName(context: Context):String? {
            return getSharedPreferences(context).getString(FIRST_NAME, null)
        }

        fun setFirstName(context: Context, firstName: String) {
            getSharedPreferences(context).edit().putString(FIRST_NAME, firstName).apply()
        }

        fun getLastName(context: Context):String? {
            return getSharedPreferences(context).getString(LAST_NAME, null)
        }

        fun setLastName(context: Context, lastName: String) {
            getSharedPreferences(context).edit().putString(LAST_NAME, lastName).apply()
        }

        fun getWithdrawalRequests(context: Context) : MutableSet<String>? {
            return getSharedPreferences(context).getStringSet(
                WITHDRAWAL_REQUESTS,
                HashSet<String>()
            )
        }

        fun setWithdrawalRequest(context: Context, value: String) {
            val list= getWithdrawalRequests(context)?.let {
                val clonedList = HashSet(it)
                clonedList.add(value)
                clonedList
            } ?:run {
                val newList = HashSet<String>()
                newList.add(value)
                newList
            }
            updateStringSet(context, list)
        }

        fun deleteWithdrawalRequest(context: Context, value: String) {
            val list = getWithdrawalRequests(context)
            list?.remove(value)
            updateStringSet(context, list)
        }

        fun clear(context: Context) {
            getSharedPreferences(context).edit().clear().apply()
        }

        private fun updateStringSet(context: Context, list: Set<String>?){
            val editor =
                getSharedPreferences(context).edit()
            editor.putStringSet(WITHDRAWAL_REQUESTS, list)
            editor.apply()
        }
    }
}