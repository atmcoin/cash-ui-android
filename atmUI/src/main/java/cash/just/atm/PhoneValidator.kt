package cash.just.atm

class PhoneValidator(private val phoneNumber:String?) {
    fun isValid() : Boolean {
        return if (phoneNumber == null || phoneNumber.length < 6 || phoneNumber.length > 13) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(phoneNumber).matches()
        }
    }

    fun phoneNumberWithoutCountryCode() : String {
        if(phoneNumber?.startsWith("+")!!) {
            if(phoneNumber[1] == '1') {
                return phoneNumber.substring(2)
            }
            else if(phoneNumber[1] == '5') {
                return phoneNumber.substring(3)
            }
        }
        return phoneNumber
    }
}