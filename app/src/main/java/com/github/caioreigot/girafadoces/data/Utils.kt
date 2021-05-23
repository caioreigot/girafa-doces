package com.github.caioreigot.girafadoces.data

import android.text.TextUtils
import android.util.Patterns

class Utils {

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
        }
    }
}