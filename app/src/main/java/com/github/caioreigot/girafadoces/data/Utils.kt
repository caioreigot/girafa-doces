package com.github.caioreigot.girafadoces.data

import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.Global

class Utils {

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
        }

        fun isValidPhoneNumber(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && target!!.length == 9
        }

        fun isRegisterInformationValid(
            fullName: String? = null,
            email: String? = null,
            phoneNumber: String? = null,
            phoneDDD: String? = null,
            deliveryAddress: String? = null,
            postalNumber: String? = null,
            password: String? = null,
            passwordConfirm: String? = null,
        ): Pair<Boolean, ErrorType?>
        {
            if (TextUtils.isEmpty(fullName) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(phoneDDD) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(deliveryAddress) ||
                TextUtils.isEmpty(postalNumber))
            {
                return Pair(false, ErrorType.EMPTY_FIELD)
            }

            if (!isValidEmail(email))
                return Pair(false, ErrorType.INVALID_EMAIL)

            if (!isValidPhoneNumber(phoneNumber))
                return Pair(false, ErrorType.INVALID_PHONE)

            if (password!!.length < Global.PASSWORD_MINIMUM_LENGTH)
                return Pair(false, ErrorType.WEAK_PASSWORD)

            if (passwordConfirm != null && password != passwordConfirm)
                return Pair(false, ErrorType.PASSWORD_CONFIRM_DONT_MATCH)

            return Pair(true, null)
        }

        fun isLoginInformationValid(
            email: String? = null,
            password: String? = null
        ): Pair<Boolean, ErrorType?>
        {
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                return Pair(false, ErrorType.EMPTY_FIELD)

            if (!isValidEmail(email))
                return Pair(false, ErrorType.INVALID_EMAIL)

            if (password!!.length < Global.PASSWORD_MINIMUM_LENGTH)
                return Pair(false, ErrorType.WEAK_PASSWORD)

            return Pair(true, null)
        }
    }
}