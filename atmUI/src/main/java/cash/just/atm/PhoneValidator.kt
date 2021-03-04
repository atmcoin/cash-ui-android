package cash.just.atm

class PhoneValidator(private val phoneNumber:String) {
    fun isValid() : Boolean {
        return if (phoneNumber.length < 6 || phoneNumber.length > 13) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(phoneNumber).matches()
        }
    }

    fun phoneNumberWithoutCountryCode() : String {
        if(phoneNumber.startsWith("+")) {
            //CA and US coutry code
            if(phoneNumber[1] == '1') {
                return phoneNumber.substring(2)
            }
            //MX country code
            else if(phoneNumber[1] == '5') {
                return phoneNumber.substring(3)
            }
            //phone number saved with old version of lib 3.X not supported, hence returning empty string
            else {
                return ""
            }
        }
        return phoneNumber
    }
}