package cash.just.atm

class PhoneValidator(private val phoneNumber:String?) {
    fun isValid() : Boolean {
        return if (phoneNumber == null || phoneNumber.length < 6 || phoneNumber.length > 13) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(phoneNumber).matches()
        }
    }
}