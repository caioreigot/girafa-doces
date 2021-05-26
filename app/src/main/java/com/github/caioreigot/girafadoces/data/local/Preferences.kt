package com.github.caioreigot.girafadoces.data.local

import android.content.Context
import android.content.SharedPreferences

class Preferences(private val context: Context) {

    private val APP_SETTINGS = "GIRAFA_DOCES_SETTINGS"
    private val PREF_EMAIL = "email"
    private val PREF_PASSWORD = "password"

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
    }

    fun getEmailAndPasswordValue(): Pair<String?, String?> {
        return Pair(
            getSharedPreferences().getString(PREF_EMAIL, null),
            getSharedPreferences().getString(PREF_PASSWORD, null)
        )
    }

    fun setEmailAndPasswordValue(email: String, password: String) {
        getSharedPreferences()
            .edit()
            .putString(PREF_EMAIL, email)
            .putString(PREF_PASSWORD, password)
            .apply()
    }

    fun clearPreferences() {
        getSharedPreferences()
            .edit()
            .clear()
            .apply()
    }

}