package `in`.engineerakash.covid19india.util

import java.util.regex.Pattern

object Validator {
    fun isValidName(name: String): Boolean {
        val nameRegex = "^[a-zA-Z\\s]{1,50}$"
        return Pattern.compile(nameRegex).matcher(name.trim { it <= ' ' }).matches()
    }

    fun isValidClientName(clientName: String?): Boolean {
        val domainRegex = "^(?!-)(?!_)([a-zA-Z\\d\\-_]{0,61})[a-zA-Z\\d]$"
        return Pattern.compile(domainRegex).matcher(clientName).matches()
    }

    fun isValidEmail(email: String?): Boolean {
        val emailRegex =
            "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        return Pattern.compile(emailRegex).matcher(email).matches()
    }

    fun isValidMobile(mobile: String?): Boolean {
        val mobileRegex = "^[6789]\\d{9}$"
        return Pattern.compile(mobileRegex).matcher(mobile).matches()
    }

    fun isValidSignupPassword(password: String?): Boolean {
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z@_\\-!\\d]{8,}$"
        return Pattern.compile(passwordRegex).matcher(password).matches()
    }

    fun isValidCompanyName(companyName: String): Boolean {
        return companyName.length >= 2
    }

    fun isValidCompanyAddress(companyAddress: String): Boolean {
        return companyAddress.length >= 10
    }

    fun isValidUsername(username: String): Boolean {
        return username.length >= 6
    }

    fun isValidPassword(password: String): Boolean {
        return password.length > 6
    }

}