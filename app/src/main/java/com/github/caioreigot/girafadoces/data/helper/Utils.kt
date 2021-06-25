package com.github.caioreigot.girafadoces.data.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.Global

class Utils {

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
        }

        fun isValidPhoneNumber(target: CharSequence?, template: CharSequence): Boolean {

            /*
             Regex for template (XX) XXXXX-XXXX
             ^[(][0-9]{2}[)][ ][0-9]{5}[-]([0-9]{4})$
             */

            val OPEN_PARENTHESES_INDEX = 0
            val CLOSED_PARENTHESES_INDEX = 1
            val WHITE_SPACE_INDEX = 2
            val DASH_INDEX = 3

            val targetInformation = mutableListOf(0, 0, 0, 0)
            val templateInformation = mutableListOf(0, 0, 0, 0)

            if (target!!.length != 15)
                return false

            // Getting information from the template
            for (i in template.indices) {
                when (template[i]) {
                    '(' -> templateInformation[OPEN_PARENTHESES_INDEX] = i
                    ')' -> templateInformation[CLOSED_PARENTHESES_INDEX] = i
                    ' ' -> templateInformation[WHITE_SPACE_INDEX] = i
                    '-' -> templateInformation[DASH_INDEX] = i
                }
            }

            // Getting information from the target
            for (i in target.indices) {
                when (target[i]) {
                    '(' -> targetInformation[OPEN_PARENTHESES_INDEX] = i
                    ')' -> targetInformation[CLOSED_PARENTHESES_INDEX] = i
                    ' ' -> targetInformation[WHITE_SPACE_INDEX] = i
                    '-' -> targetInformation[DASH_INDEX] = i
                }
            }

            // Validating
            return (targetInformation == templateInformation)
        }

        fun isRegisterInformationValid(
            fullName: String? = null,
            email: String? = null,
            phoneNumber: String? = null,
            deliveryAddress: String? = null,
            postalNumber: String? = null,
            password: String? = null,
            passwordConfirm: String? = null,
        ): Pair<Boolean, ErrorType?>
        {
            if (TextUtils.isEmpty(fullName) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(deliveryAddress) ||
                TextUtils.isEmpty(postalNumber))
            {
                return Pair(false, ErrorType.EMPTY_FIELD)
            }

            if (!isValidEmail(email))
                return Pair(false, ErrorType.INVALID_EMAIL)

            if (!isValidPhoneNumber(phoneNumber, "(XX) XXXXX-XXXX"))
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

        fun ByteArray.toBitmap(): Bitmap {
            return BitmapFactory.decodeByteArray(this, 0, this.size)
        }

        fun EditText.putCharSequenceBefore(cs: CharSequence) {
            var newText = ""
            var amountCharAdded = 0

            for (i in 0 .. this.text.length)
                if (i == this.text.length - 1) {
                    newText += cs
                    amountCharAdded++
                } else
                    newText += text[i - amountCharAdded]

            this.setText(newText)
        }

        fun EditText.removeLastWord() {
            var newText = ""

            for (i in this.text.indices) {
                if (i == this.text.indices.last)
                    continue
                else
                    newText += this.text[i]
            }

            this.setText(newText)
        }
    }
}