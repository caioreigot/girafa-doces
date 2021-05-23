package com.github.caioreigot.girafadoces.data

import android.text.TextUtils
import android.util.Patterns
import com.github.caioreigot.girafadoces.data.model.Global

class Utils {

    companion object {
        private fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
        }

        private fun isValidFullName(target: CharSequence?): Boolean {
            val regex = Regex("^[a-zA-Z]{4,}(?: [a-zA-Z]+){0,2}$")
            target?.let { return target.matches(regex) }
            return false
        }

        fun infoVerification(
            fullName: String? = null,
            email: String? = null,
            password: String? = null,
            passwordConfirm: String? = null,
        ): Pair<Boolean, String> {

            if (fullName != null && !isValidFullName(fullName))
                return Pair(false, "Nome não válido")

            if (email != null && !isValidEmail(email))
                return Pair(false, "E-mail não válido")

            if (password != null) {
                if (TextUtils.isEmpty(password))
                    return Pair(false, "Senha não pode estar vazia")

                if (password.length < Global.PASSWORD_MINIMUM_LENGTH)
                    return Pair(false, "Senha não pode ser menor que 6 digitos")

                if (password != passwordConfirm)
                    return Pair(false, "Sua confirmação de senha não é igual à sua senha")
            }

            return Pair(true, "Informações válidas")
        }
    }
}